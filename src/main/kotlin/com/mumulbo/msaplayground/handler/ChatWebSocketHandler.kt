package com.mumulbo.msaplayground.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mumulbo.msaplayground.common.logger
import com.mumulbo.msaplayground.model.ChatMessage
import com.mumulbo.msaplayground.model.MessageType
import com.mumulbo.msaplayground.service.RedisPublisher
import com.mumulbo.msaplayground.service.MemberServiceClient
import org.springframework.stereotype.Component
import org.springframework.web.socket.*
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.time.LocalDateTime

@Component
class ChatWebSocketHandler(
    private val objectMapper: ObjectMapper,
    private val redisPublisher: RedisPublisher,
    private val sessionManager: WebSocketSessionManager,
    private val memberServiceClient: MemberServiceClient,
) : TextWebSocketHandler() {

    private val log = logger()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        log.info("[Chat-Service] WebSocket connection attempt - sessionId={}", session.id)

        val userId = session.uri.query
            ?.split("&")
            ?.map { it.split("=") }
            ?.associate { it[0] to it.getOrNull(1) }
            ?.get("userId")

        if (userId.isNullOrBlank()) {
            log.warn("[Chat-Service] Missing userId in WebSocket connection - sessionId={}", session.id)
            session.close(CloseStatus.POLICY_VIOLATION)
            return
        }

        try {
            val member = memberServiceClient.getMemberInfo(userId.toLong())
            session.attributes["userId"] = userId.toLong()
            session.attributes["nickname"] = member.nickname
            session.attributes["email"] = member.email

            log.info("[Chat-Service] WebSocket connection established - sessionId={}, nickname={}", session.id, member.nickname)
            sessionManager.add(session)

            // 접속자 목록 브로드캐스트
            broadcastUserListUpdate()
        } catch (e: Exception) {
            log.error("[Chat-Service] Failed to fetch member info - userId={}, error={}", userId, e.message, e)
            session.close(CloseStatus.SERVER_ERROR)
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        log.info("[Chat-Service] WebSocket connection closed - sessionId={}, status={}", session.id, status)
        sessionManager.remove(session)

        //접속자 목록 실시간 전송
        broadcastUserListUpdate()
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        log.info("[Chat-Service] Received WebSocket message - sessionId={}, payload={}", session.id, message.payload)

        try {
            val chatMessage: ChatMessage = objectMapper.readValue(message.payload)

            chatMessage.senderName = session.attributes["nickname"] as? String ?: "unknown"
            chatMessage.senderEmail = session.attributes["email"] as? String ?: "unknown"
            val senderId = session.attributes["userId"] as? Long ?: -1L

            when (chatMessage.type) {
                MessageType.WHISPER -> {
                    val recipientId = chatMessage.recipientUserId
                    if (recipientId == null) {
                        log.warn("[Chat-Service] Whisper ignored - recipientUserId is null (sessionId={})", session.id)
                        return
                    }

                    // 자기 자신에게 귓속말 금지
                    if (senderId == recipientId) {
                        log.warn("[Chat-Service] Whisper blocked - sender tried to whisper to self (userId={})", senderId)

                        val failMessage = ChatMessage(
                            type = MessageType.WHISPER_FAILED,
                            message = "귓속말 전송 실패: 자기 자신에게는 보낼 수 없습니다.",
                            recipientUserId = recipientId,
                            sentAt = LocalDateTime.now()
                        )
                        val failJson = objectMapper.writeValueAsString(failMessage)
                        session.sendMessage(TextMessage(failJson))
                        return
                    }

                    val targetSession = sessionManager.getSessionByUserId(recipientId)
                    if (targetSession != null && targetSession.isOpen) {
                        log.info("[Chat-Service] Whisper sent - fromUserId={}, toUserId={}, message={}", senderId, recipientId, chatMessage.message)

                        val whisperJson = objectMapper.writeValueAsString(chatMessage)
                        session.sendMessage(TextMessage(whisperJson))
                        targetSession.sendMessage(TextMessage(whisperJson))

                        redisPublisher.publish("chat-room:main", chatMessage)
                    } else {
                        log.warn("[Chat-Service] Whisper failed - target not found or closed (recipientUserId={})", recipientId)

                        val failMessage = ChatMessage(
                            type = MessageType.WHISPER_FAILED,
                            message = "귓속말 전송 실패: 상대가 접속 중이 아닙니다.",
                            recipientUserId = recipientId,
                            sentAt = LocalDateTime.now()
                        )
                        val failJson = objectMapper.writeValueAsString(failMessage)
                        session.sendMessage(TextMessage(failJson))
                    }
                }

                else -> {
                    redisPublisher.publish("chat-room:main", chatMessage)
                    log.debug("[Chat-Service] Message published to Redis - sender={}, roomId={}", chatMessage.senderName, chatMessage.roomId)
                }
            }
        } catch (e: Exception) {
            log.error("[Chat-Service] Failed to parse incoming message - sessionId={}, error={}", session.id, e.message, e)
        }
    }

    private fun broadcastUserListUpdate() {
        val connectedUserIds = sessionManager.getAllConnectedUserIds()

        val updateMessage = ChatMessage(
            type = MessageType.USER_LIST_UPDATE,
            connectedUserList = connectedUserIds,
            sentAt = LocalDateTime.now()
        )

        redisPublisher.publish("chat-room:main", updateMessage)
    }

    private val WebSocketSession.headers: Map<String, List<String>>
        get() = (attributes["org.springframework.http.HttpHeaders"] as? Map<String, List<String>>)
            ?: emptyMap()
}
