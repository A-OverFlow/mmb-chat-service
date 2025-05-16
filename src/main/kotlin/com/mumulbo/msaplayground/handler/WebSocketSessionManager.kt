package com.mumulbo.msaplayground.handler

import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Component
class WebSocketSessionManager {
    private val sessions: MutableMap<String, WebSocketSession> = ConcurrentHashMap()

    fun add(session: WebSocketSession) {
        sessions[session.id] = session
    }

    fun remove(session: WebSocketSession) {
        sessions.remove(session.id)
    }

    fun broadcast(message: String) {
        sessions.values.forEach {
            if (it.isOpen) {
                it.sendMessage(org.springframework.web.socket.TextMessage(message))
            }
        }
    }
}
