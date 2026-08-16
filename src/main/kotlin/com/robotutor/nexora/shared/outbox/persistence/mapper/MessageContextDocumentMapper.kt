package com.robotutor.nexora.shared.outbox.persistence.mapper

import com.robotutor.nexora.shared.message.config.EventName
import com.robotutor.nexora.shared.message.message.MessageContext
import com.robotutor.nexora.shared.outbox.persistence.document.*
import com.robotutor.nexora.shared.outbox.vo.EventId

object MessageContextDocumentMapper {
    fun toDocument(context: MessageContext): MessageContextDocument {
        return MessageContextDocument(
            eventName = context.eventName.topic,
            correlationId = context.correlationId,
            principalData = context.principalData?.let { PrincipalDataDocumentMapper.toDocument(it) },
            eventId = context.eventId.value,
            occurredAt = context.occurredAt
        )
    }

    fun toDomain(document: MessageContextDocument): MessageContext {
        return MessageContext(
            eventName = EventName.from(document.eventName),
            correlationId = document.correlationId,
            principalData = document.principalData?.let { PrincipalDataDocumentMapper.toDomain(it) },
            eventId = EventId(document.eventId),
            occurredAt = document.occurredAt
        )
    }
}
