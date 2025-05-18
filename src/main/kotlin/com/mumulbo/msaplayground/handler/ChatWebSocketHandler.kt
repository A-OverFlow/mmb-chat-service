package com.mumulbo.msaplayground.handler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mumulbo.msaplayground.model.ChatMessage
import com.mumulbo.msaplayground.service.RedisPublisher
import org.springframework.stereotype.Component
import org.springframework.web.socket.*
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChatWebSocketHandler(
    private val redisPublisher: RedisPublisher,
    private val sessionManager: WebSocketSessionManager
) : TextWebSocketHandler() {

    private val objectMapper = jacksonObjectMapper()

    override fun afterConnectionEstablished(session: WebSocketSession) {
//        val token = session.uri?.query?.split("token=")?.getOrNull(1)
//        if (token == null || !validateToken(token)) {
//            session.close(CloseStatus.POLICY_VIOLATION)
//            return
//        }

        println("✅ WebSocket 연결 성공: ${session.id}")
        sessionManager.add(session) // 추가
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionManager.remove(session) // 제거
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        println("📨 받은 메시지: ${message.payload}")

        try {
            val chatMessage: ChatMessage = objectMapper.readValue(message.payload)
            // TODO: senderName, senderEmail은 이후 JWT 인증 정보로 설정
            redisPublisher.publish("chat-room:main", chatMessage)
        } catch (e: Exception) {
            println("❌ 메시지 파싱 실패: ${e.message}")
        }
    }

    private fun validateToken(token: String): Boolean {
        return token.startsWith("valid")
    }
}
