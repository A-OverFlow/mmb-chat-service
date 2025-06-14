package com.mumulbo.msaplayground.config

import jakarta.annotation.PostConstruct
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class MongoTTLIndexInitializer(
    private val mongoTemplate: MongoTemplate
) {

    @PostConstruct
    fun registerTTLIndex() {
        mongoTemplate.indexOps("chat_messages").ensureIndex(
            Index("sentAt", Sort.Direction.ASC).expire(12, TimeUnit.HOURS)
        )
    }
}