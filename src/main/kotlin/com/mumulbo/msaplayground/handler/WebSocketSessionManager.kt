package com.mumulbo.msaplayground.handler

import com.mumulbo.msaplayground.common.logger
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Component
class WebSocketSessionManager {

    private val log = logger()
    private val sessions: MutableMap<String, WebSocketSession> = ConcurrentHashMap()

    fun add(session: WebSocketSession) {
        sessions[session.id] = session
        log.info("[Chat-Service] WebSocket session added - sessionId={}, totalSessions={}", session.id, sessions.size)
    }

    fun remove(session: WebSocketSession) {
        sessions.remove(session.id)
        log.info("[Chat-Service] WebSocket session removed - sessionId={}, totalSessions={}", session.id, sessions.size)
    }

    fun broadcast(message: String) {
        log.info("[Chat-Service] Broadcasting message to {} session(s)", sessions.size)

        sessions.values.forEach {
            if (it.isOpen) {
                try {
                    it.sendMessage(TextMessage(message))
                } catch (e: Exception) {
                    log.error("[Chat-Service] Failed to send message to sessionId={} - error={}", it.id, e.message, e)
                }
            }
        }
    }
}
