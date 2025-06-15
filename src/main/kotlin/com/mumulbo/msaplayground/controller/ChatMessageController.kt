package com.mumulbo.msaplayground.controller

import com.mumulbo.msaplayground.common.logger
import com.mumulbo.msaplayground.dto.ChatMessageRequest
import com.mumulbo.msaplayground.model.ChatMessage
import com.mumulbo.msaplayground.model.MessageType
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

    private val log = logger()

    @GetMapping("/messages")
    fun getTextMessages(): ResponseEntity<List<ChatMessage>> {
        val messages = chatMessageRepository.findByType(MessageType.TEXT)
            .sortedByDescending { it.sentAt }

        return ResponseEntity.ok(messages)
    }

    @PostMapping("/message")
    fun postChatMessage(@RequestBody request: ChatMessageRequest): ResponseEntity<ChatMessage> {
        log.info("[Chat-Service] Received request to save chat message - sender={}, roomId={}", request.senderName, request.roomId)

        val chatMessage = ChatMessage(
            senderName = request.senderName,
            senderEmail = request.senderEmail,
            message = request.message,
            type = request.type,
            roomId = request.roomId
        )

        val saved = chatMessageRepository.save(chatMessage)

        log.info("[Chat-Service] Chat message saved - messageId={}", saved.id)

        return ResponseEntity.ok(saved)
    }
}
