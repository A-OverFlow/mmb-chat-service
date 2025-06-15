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
    private val userIdToSessionId: MutableMap<Long, String> = ConcurrentHashMap()

    fun add(session: WebSocketSession) {
        sessions[session.id] = session

        val userId = session.attributes["userId"] as? Long
        if (userId != null) {
            userIdToSessionId[userId] = session.id
            log.info("[Chat-Service] WebSocket session added - sessionId={}, userId={}, totalSessions={}", session.id, userId, sessions.size)
        } else {
            log.warn("[Chat-Service] WebSocket session added without userId - sessionId={}, totalSessions={}", session.id, sessions.size)
        }
    }

    fun remove(session: WebSocketSession) {
        sessions.remove(session.id)

        val userId = session.attributes["userId"] as? Long
        if (userId != null) {
            userIdToSessionId.remove(userId)
            log.info("[Chat-Service] WebSocket session removed - sessionId={}, userId={}, totalSessions={}", session.id, userId, sessions.size)
        } else {
            log.warn("[Chat-Service] WebSocket session removed without userId - sessionId={}, totalSessions={}", session.id, sessions.size)
        }
    }

    fun getSessionByUserId(userId: Long): WebSocketSession? {
        val sessionId = userIdToSessionId[userId]
        return sessionId?.let { sessions[it] }
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
