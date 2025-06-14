package com.mumulbo.msaplayground.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.mumulbo.msaplayground.common.logger
import com.mumulbo.msaplayground.model.ChatMessage
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class RedisPublisher(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper
) {
    private val log = logger()

    fun publish(channel: String, message: ChatMessage) {
        try {
            val json = objectMapper.writeValueAsString(message)
            redisTemplate.convertAndSend(channel, json)

            log.debug(
                "[Chat-Service] Published message to Redis - channel={}, sender={}, roomId={}",
                channel, message.senderName, message.roomId
            )
        } catch (e: Exception) {
            log.error(
                "[Chat-Service] Failed to publish message to Redis - channel={}, error={}",
                channel, e.message, e
            )
        }
    }
}