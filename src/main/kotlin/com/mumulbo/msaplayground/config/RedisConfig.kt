package com.mumulbo.msaplayground.config

import com.mumulbo.msaplayground.common.logger
import com.mumulbo.msaplayground.subscriber.RedisSubscriber
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer

@Configuration
class RedisConfig {

    private val log = logger()

    @Bean
    fun redisContainer(
        connectionFactory: RedisConnectionFactory,
        redisSubscriber: RedisSubscriber
    ): RedisMessageListenerContainer {
        val topic = "chat-room:main"

        log.info("[Chat-Service] Initializing Redis message listener container - topic={}", topic)

        return try {
            val container = RedisMessageListenerContainer()
            container.setConnectionFactory(connectionFactory)
            container.addMessageListener(redisSubscriber, ChannelTopic(topic))

            log.info("[Chat-Service] Redis message listener registered - topic={}", topic)
            container
        } catch (e: Exception) {
            log.error("[Chat-Service] Failed to set up Redis listener container - topic={}, error={}", topic, e.message, e)
            throw e
        }
    }
}