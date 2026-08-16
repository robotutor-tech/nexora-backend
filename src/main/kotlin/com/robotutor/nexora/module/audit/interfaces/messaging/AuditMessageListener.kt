package com.robotutor.nexora.module.audit.interfaces.messaging

import com.robotutor.nexora.module.audit.application.service.AddAuditService
import com.robotutor.nexora.module.audit.domain.aggregate.Audit
import com.robotutor.nexora.module.audit.interfaces.messaging.mapper.AuditMapper
import com.robotutor.nexora.shared.message.annotation.EventController
import com.robotutor.nexora.shared.message.annotation.EventListener
import com.robotutor.nexora.shared.message.annotation.Message
import com.robotutor.nexora.shared.message.config.EventName
import com.robotutor.nexora.shared.message.consumer.KafkaConsumer
import com.robotutor.nexora.shared.message.message.MessageContext
import com.robotutor.nexora.shared.outbox.audit.AuditEventMessage
import reactor.core.publisher.Mono

@Suppress("unused")
@EventController
class AuditMessageListener(private val addAuditService: AddAuditService) : KafkaConsumer {

    @EventListener([EventName.AUDITORY])
    fun createAuditRecord(@Message message: AuditEventMessage, context: MessageContext): Mono<Audit> {
        val command = AuditMapper.toAddAuditCommand(message, context)
        return addAuditService.execute(command)
    }
}
