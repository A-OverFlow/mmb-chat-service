package com.mumulbo.msaplayground.handler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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
    private val memberServiceClient: MemberServiceClient, // FeignClient
) : TextWebSocketHandler() {

    private val objectMapper = jacksonObjectMapper()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val userId = session.uri.query
            ?.split("&")
            ?.map { it.split("=") }
            ?.associate { it[0] to it.getOrNull(1) }
            ?.get("userId")

        if (userId.isNullOrBlank()) {
            println("❌ X-User-Id 누락")
            session.close(CloseStatus.POLICY_VIOLATION)
            return
        }

        try {
            val member = memberServiceClient.getMemberInfo(userId.toLong())

            session.attributes["nickname"] = member.nickname
            session.attributes["email"] = member.email

            println("✅ WebSocket 연결 성공: ${session.id} (닉네임: ${member.nickname})")
            sessionManager.add(session)
        } catch (e: Exception) {
            println("❌ 멤버 정보 조회 실패: ${e.message}")
            session.close(CloseStatus.SERVER_ERROR)
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionManager.remove(session)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        println("📨 받은 메시지: ${message.payload}")

        try {
            val chatMessage: ChatMessage = objectMapper.readValue(message.payload)

            chatMessage.senderName = session.attributes["nickname"] as? String ?: "unknown"
            chatMessage.senderEmail = session.attributes["email"] as? String ?: "unknown"

            redisPublisher.publish("chat-room:main", chatMessage)
        } catch (e: Exception) {
            println("❌ 메시지 파싱 실패: ${e.message}")
        }
    }

    // WebSocketSession에서 헤더 추출하는 유틸 (Spring WebSocket 기본 구현에는 headers 접근자 없음)
    private val WebSocketSession.headers: Map<String, List<String>>
        get() = (attributes["org.springframework.http.HttpHeaders"] as? Map<String, List<String>>)
            ?: emptyMap()
}
