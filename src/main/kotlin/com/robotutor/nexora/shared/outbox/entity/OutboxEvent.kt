package com.robotutor.nexora.shared.outbox.entity

import com.robotutor.nexora.shared.domain.Entity
import com.robotutor.nexora.shared.message.message.EventMessage
import com.robotutor.nexora.shared.outbox.vo.EventId
import java.time.Instant

data class OutboxEvent(
    val message: EventMessage,
    val eventId: EventId = EventId.generateId(),
    val occurredAt: Instant = Instant.now(),
) : Entity<OutboxEvent, EventId>(eventId)
