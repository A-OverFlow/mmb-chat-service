package com.mumulbo.msaplayground.controller

import com.mumulbo.msaplayground.model.ChatMessage
import com.mumulbo.msaplayground.repository.ChatMessageRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@RestController
@RequestMapping("/chat")
class ChatMessageController(
    private val chatMessageRepository: ChatMessageRepository
) {

    @GetMapping("/messages")
    fun getRecentMessages(): ResponseEntity<List<ChatMessage>> {
        val now = Instant.now()
        val cutoff = now.minus(12, ChronoUnit.HOURS)
        val messages = chatMessageRepository
            .findBySentAtAfterOrderBySentAtAsc(Date.from(cutoff))
        return ResponseEntity.ok(messages)
    }
}
