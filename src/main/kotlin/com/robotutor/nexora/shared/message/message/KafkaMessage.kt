package com.robotutor.nexora.shared.message.message

import com.robotutor.nexora.shared.domain.vo.PrincipalData
import com.robotutor.nexora.shared.outbox.vo.EventId
import java.time.Instant

interface KafkaMessage : EventMessage {
    val eventId: EventId
    val occurredAt: Instant
    val correlationId: String
    val principalData: PrincipalData?
}

