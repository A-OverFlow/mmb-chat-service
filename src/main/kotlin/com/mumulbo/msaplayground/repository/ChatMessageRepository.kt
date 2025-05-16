package com.mumulbo.msaplayground.repository

import com.mumulbo.msaplayground.model.ChatMessage
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChatMessageRepository : MongoRepository<ChatMessage, String> {
    fun findBySentAtAfterOrderBySentAtAsc(sentAt: Date): List<ChatMessage>
}