package com.mumulbo.msaplayground.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.mumulbo.msaplayground.model.ChatMessage
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class RedisPublisher(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper
) {
    fun publish(channel: String, message: ChatMessage) {
        val json = objectMapper.writeValueAsString(message)
        redisTemplate.convertAndSend(channel, json)
    }
}
