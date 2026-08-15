package com.robotutor.nexora.shared.message.message

import com.robotutor.nexora.shared.outbox.persistence.document.OutboxDocument
import com.robotutor.nexora.shared.outbox.persistence.document.PrincipalDataDocument
import java.time.Instant

data class KafkaMessage(
    val eventId: String,
    val eventMessage: EventMessage,
    val occurredAt: Instant,
    val correlationId: String,
    val principalData: PrincipalDataDocument?,
) {
    companion object {
        fun from(document: OutboxDocument): KafkaMessage {
            return KafkaMessage(
                eventId = document.eventId,
                eventMessage = document.message,
                occurredAt = document.occurredAt,
                correlationId = document.correlationId,
                principalData = document.principalData,
            )
        }
    }
}
