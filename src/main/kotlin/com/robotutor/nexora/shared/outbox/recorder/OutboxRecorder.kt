package com.robotutor.nexora.shared.outbox.recorder

import com.robotutor.nexora.shared.context.ReactiveContext
import com.robotutor.nexora.shared.persistence.repository.retryOptimisticLockingFailure
import com.robotutor.nexora.shared.outbox.persistence.document.OutboxDocument
import com.robotutor.nexora.shared.outbox.persistence.repository.OutboxDocumentRepository
import com.robotutor.nexora.shared.outbox.entity.OutboxEvent
import com.robotutor.nexora.shared.outbox.persistence.mapper.PrincipalDataDocumentMapper
import com.robotutor.nexora.shared.utility.createMono
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class OutboxRecorder(private val outboxRepository: OutboxDocumentRepository) : Recorder {
    override fun record(message: OutboxEvent): Mono<OutboxDocument> {
        return ReactiveContext.getContextData()
            .map {
                OutboxDocument(
                    message = message.message,
                    correlationId = it.correlationId,
                    eventId = message.eventId.value,
                    occurredAt = message.occurredAt,
                    principalData = it.principalData?.let { PrincipalDataDocumentMapper.toMongoDocument(it) }
                )
            }
            .flatMap { outboxRepository.save(it) }
            .retryOptimisticLockingFailure()
    }
}
