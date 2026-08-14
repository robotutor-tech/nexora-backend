package com.robotutor.nexora.shared.outbox.recorder

import com.robotutor.nexora.shared.persistence.repository.retryOptimisticLockingFailure
import com.robotutor.nexora.shared.outbox.persistence.document.OutboxDocument
import com.robotutor.nexora.shared.outbox.persistence.repository.OutboxDocumentRepository
import com.robotutor.nexora.shared.application.ReactiveContext
import com.robotutor.nexora.shared.message.message.EventMessage
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Instant

@Component
class OutboxRecorder(
    private val outboxRepository: OutboxDocumentRepository
) : Recorder {
    override fun record(message: EventMessage): Mono<OutboxDocument> {
        return ReactiveContext.getTraceData()
            .map {
                OutboxDocument(
                    eventId = "message.eventId",
                    correlationId = it.correlationId,
                    message = message,
                    occurredAt = Instant.now() //message.occurredAt
                )
            }
            .flatMap { outboxRepository.save(it) }
            .retryOptimisticLockingFailure()
    }
}
