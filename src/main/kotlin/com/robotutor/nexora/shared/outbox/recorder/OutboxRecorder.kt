package com.robotutor.nexora.shared.outbox.recorder

import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
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

    private val logger = Logger(this::class.java)

    override fun record(message: EventMessage): Mono<OutboxDocument> {
        return ReactiveContext.getContextData()
            .map { MessageContext(message.eventName, it.correlationId, it.principalData) }
            .map {
                val contextDocument = MessageContextDocumentMapper.toDocument(it)
                OutboxDocument(eventId = contextDocument.eventId, message = message, context = contextDocument)
            }
            .flatMap {
                val additionalDetails = mapOf("eventId" to it.context.eventId, "eventName" to it.context.eventName)
                outboxRepository.save(it)
//                    .retryOptimisticLockingFailure()
                    .logOnSuccess(logger, "Successfully added event to outbox", additionalDetails)
                    .logOnError(logger, "Failed to add event to outbox", additionalDetails)
            }
    }
}
