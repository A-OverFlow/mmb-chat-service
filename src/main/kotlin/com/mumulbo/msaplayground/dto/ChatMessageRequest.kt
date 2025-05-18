package com.mumulbo.msaplayground.dto

import com.mumulbo.msaplayground.model.MessageType

data class ChatMessageRequest(
    val senderName: String,
    val senderEmail: String,
    val message: String,
    val type: MessageType,
    val roomId: String = "main"
)
