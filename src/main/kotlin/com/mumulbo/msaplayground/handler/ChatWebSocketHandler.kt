package com.mumulbo.msaplayground.handler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mumulbo.msaplayground.common.logger
import com.mumulbo.msaplayground.model.ChatMessage
import com.mumulbo.msaplayground.service.RedisPublisher
import com.mumulbo.msaplayground.service.MemberServiceClient
import org.springframework.stereotype.Component
import org.springframework.web.socket.*
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChatWebSocketHandler(
    private val redisPublisher: RedisPublisher,
    private val sessionManager: WebSocketSessionManager,
    private val memberServiceClient: MemberServiceClient,
) : TextWebSocketHandler() {

    private val objectMapper = jacksonObjectMapper()
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
            session.attributes["nickname"] = member.nickname
            session.attributes["email"] = member.email

            log.info("[Chat-Service] WebSocket connection established - sessionId={}, nickname={}", session.id, member.nickname)
            sessionManager.add(session)
        } catch (e: Exception) {
            log.error("[Chat-Service] Failed to fetch member info - userId={}, error={}", userId, e.message, e)
            session.close(CloseStatus.SERVER_ERROR)
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        log.info("[Chat-Service] WebSocket connection closed - sessionId={}, status={}", session.id, status)
        sessionManager.remove(session)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        log.info("[Chat-Service] Received WebSocket message - sessionId={}, payload={}", session.id, message.payload)

        try {
            val chatMessage: ChatMessage = objectMapper.readValue(message.payload)

            chatMessage.senderName = session.attributes["nickname"] as? String ?: "unknown"
            chatMessage.senderEmail = session.attributes["email"] as? String ?: "unknown"

            redisPublisher.publish("chat-room:main", chatMessage)
            log.debug("[Chat-Service] Message published to Redis - sender={}, roomId={}", chatMessage.senderName, chatMessage.roomId)
        } catch (e: Exception) {
            log.error("[Chat-Service] Failed to parse incoming message - sessionId={}, error={}", session.id, e.message, e)
        }
    }

    private val WebSocketSession.headers: Map<String, List<String>>
        get() = (attributes["org.springframework.http.HttpHeaders"] as? Map<String, List<String>>)
            ?: emptyMap()
}
