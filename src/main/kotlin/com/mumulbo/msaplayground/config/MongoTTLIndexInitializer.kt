package com.mumulbo.msaplayground.config

import com.mumulbo.msaplayground.common.logger
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
    private val log = logger()

    @PostConstruct
    fun registerTTLIndex() {
        val collectionName = "chat_messages"
        val ttlHours = 12L

        log.info("[Chat-Service] Starting TTL index registration - collection={}, field=sentAt, expireAfter={}h", collectionName, ttlHours)

        try {
            mongoTemplate.indexOps(collectionName).ensureIndex(
                Index("sentAt", Sort.Direction.ASC).expire(ttlHours, TimeUnit.HOURS)
            )
            log.info("[Chat-Service] TTL index registered successfully - collection={}, field=sentAt", collectionName)
        } catch (e: Exception) {
            log.error("[Chat-Service] Failed to register TTL index - collection={}, error={}", collectionName, e.message, e)
        }
    }
}