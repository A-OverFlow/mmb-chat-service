package com.mumulbo.msaplayground.controller

import com.mumulbo.msaplayground.dto.ChatMessageRequest
import com.mumulbo.msaplayground.model.ChatMessage
import com.mumulbo.msaplayground.repository.ChatMessageRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@RestController
@RequestMapping("/api/v1/chat")
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

    @PostMapping("/message")
    fun postChatMessage(@RequestBody request: ChatMessageRequest): ResponseEntity<ChatMessage> {
        val chatMessage = ChatMessage(
            senderName = request.senderName,
            senderEmail = request.senderEmail,
            message = request.message,
            type = request.type,
            roomId = request.roomId
        )

        val saved = chatMessageRepository.save(chatMessage)
        return ResponseEntity.ok(saved)
    }
}
