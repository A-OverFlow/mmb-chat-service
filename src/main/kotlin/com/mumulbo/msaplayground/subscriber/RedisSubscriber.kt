package com.mumulbo.msaplayground.subscriber

import com.fasterxml.jackson.databind.ObjectMapper
import com.mumulbo.msaplayground.handler.WebSocketSessionManager
import com.mumulbo.msaplayground.model.ChatMessage
import com.mumulbo.msaplayground.repository.ChatMessageRepository
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Service
import com.mumulbo.msaplayground.common.logger

@Service
class RedisSubscriber(
    private val sessionManager: WebSocketSessionManager,
    private val objectMapper: ObjectMapper,
    private val chatMessageRepository: ChatMessageRepository
) : MessageListener {

    private val log = logger()

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val body = String(message.body)
        log.info("📡 Redis에서 메시지 수신: {}", body)

        try {
            val chatMessage = objectMapper.readValue(body, ChatMessage::class.java)

            // MongoDB 저장
            chatMessageRepository.save(chatMessage)

            // WebSocket 브로드캐스트
            sessionManager.broadcast(body)

        } catch (e: Exception) {
            log.error("❌ RedisSubscriber 파싱/저장 실패", e)
        }
    }
}

