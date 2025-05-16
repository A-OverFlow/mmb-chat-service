package com.mumulbo.msaplayground.model

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat_messages")
data class ChatMessage(
    @Id
    val id: String? = null,
    val senderId: String? = null,
    val senderNickname: String? = null,
    val message: String,
    val type: MessageType,
    val roomId: String = "main",

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val sentAt: LocalDateTime = LocalDateTime.now()
)

enum class MessageType {
    TEXT, EMOJI, SYSTEM, WHISPER
}
