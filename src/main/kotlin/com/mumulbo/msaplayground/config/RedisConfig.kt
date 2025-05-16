package com.mumulbo.msaplayground.config

import com.mumulbo.msaplayground.subscriber.RedisSubscriber
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer

@Configuration
class RedisConfig {

    @Bean
    fun redisContainer(
        connectionFactory: RedisConnectionFactory,
        redisSubscriber: RedisSubscriber
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(connectionFactory)
        container.addMessageListener(redisSubscriber, ChannelTopic("chat-room:main"))
        return container
    }
}
