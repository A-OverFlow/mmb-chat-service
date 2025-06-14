package com.mumulbo.msaplayground.config

import com.mumulbo.msaplayground.common.logger
import com.mumulbo.msaplayground.handler.ChatWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.*

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val chatWebSocketHandler: ChatWebSocketHandler
) : WebSocketConfigurer {

    private val log = logger()

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        val path = "/ws/chat"
        val origins = "*"

        log.info("[Chat-Service] Registering WebSocket handler - path={}, allowedOrigins={}", path, origins)

        try {
            registry.addHandler(chatWebSocketHandler, path)
                .setAllowedOrigins(origins)

            log.info("[Chat-Service] WebSocket handler registered successfully - path={}", path)
        } catch (e: Exception) {
            log.error("[Chat-Service] Failed to register WebSocket handler - path={}, error={}", path, e.message, e)
            throw e
        }
    }
}
