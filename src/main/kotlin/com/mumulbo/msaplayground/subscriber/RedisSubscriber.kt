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
        log.info("[Chat-Service] Received message from Redis - payload={}", body)

        try {
            val chatMessage = objectMapper.readValue(body, ChatMessage::class.java)
            log.debug("[Chat-Service] Parsed ChatMessage - sender={}, roomId={}", chatMessage.senderName, chatMessage.roomId)

            chatMessageRepository.save(chatMessage)
            log.debug("[Chat-Service] ChatMessage saved to MongoDB - messageId={}", chatMessage.id)

            sessionManager.broadcast(body)
            log.debug("[Chat-Service] Broadcasted message to WebSocket clients")
        } catch (e: Exception) {
            log.error("[Chat-Service] Failed to process Redis message - error={}", e.message, e)
        }
    }
}

