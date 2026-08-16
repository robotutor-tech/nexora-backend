package com.robotutor.nexora.shared.outbox.recorder

import com.robotutor.nexora.shared.context.ReactiveContext
import com.robotutor.nexora.shared.message.message.EventMessage
import com.robotutor.nexora.shared.message.message.MessageContext
import com.robotutor.nexora.shared.outbox.persistence.document.OutboxDocument
import com.robotutor.nexora.shared.outbox.persistence.mapper.MessageContextDocumentMapper
import com.robotutor.nexora.shared.outbox.persistence.repository.OutboxDocumentRepository
import com.robotutor.nexora.shared.persistence.repository.retryOptimisticLockingFailure
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class OutboxRecorder(private val outboxRepository: OutboxDocumentRepository) : Recorder {
    override fun record(message: EventMessage): Mono<OutboxDocument> {
        return ReactiveContext.getContextData()
            .map { MessageContext(message.eventName, it.correlationId, it.principalData) }
            .map {
                val contextDocument = MessageContextDocumentMapper.toDocument(it)
                OutboxDocument(eventId = contextDocument.eventId, message = message, context = contextDocument)
            }
            .flatMap { outboxRepository.save(it) }
            .retryOptimisticLockingFailure()
    }
}
