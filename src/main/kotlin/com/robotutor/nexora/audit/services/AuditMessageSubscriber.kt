package com.robotutor.nexora.audit.services

import com.robotutor.nexora.kafka.models.AuditMessage
import com.robotutor.nexora.kafka.services.KafkaConsumer
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class AuditMessageSubscriber(
    private val auditService: AuditService,
    private val kafkaConsumer: KafkaConsumer,
) {

    @PostConstruct
    fun subscribe() {
        kafkaConsumer.consume(listOf("AUDIT"), AuditMessage::class.java) {
            auditService.addAudit(it.message)
        }
            .subscribe()
    }
}