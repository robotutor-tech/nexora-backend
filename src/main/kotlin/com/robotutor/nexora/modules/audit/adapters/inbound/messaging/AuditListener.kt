package com.robotutor.nexora.modules.audit.adapters.inbound.messaging

import com.robotutor.nexora.modules.audit.adapters.inbound.messaging.dto.AuditMessage
import com.robotutor.nexora.modules.audit.adapters.inbound.messaging.mapper.AuditMapper
import com.robotutor.nexora.modules.audit.application.SaveAuditMessageUseCase
import com.robotutor.nexora.shared.adapters.messaging.services.KafkaConsumer
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class AuditListener(
    private val saveAuditMessageUseCase: SaveAuditMessageUseCase,
    private val kafkaConsumer: KafkaConsumer,
) {

    @PostConstruct
    fun subscribe() {
        kafkaConsumer.consume(listOf("AUDIT"), AuditMessage::class.java) {
            saveAuditMessageUseCase.addAudit(AuditMapper.toAuditDetails(it.message))
        }
            .subscribe()
    }
}
