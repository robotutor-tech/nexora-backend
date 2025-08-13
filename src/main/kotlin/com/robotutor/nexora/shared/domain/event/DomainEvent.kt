package com.robotutor.nexora.shared.domain.event

import java.time.Instant
import java.util.UUID

open class DomainEvent {
    val id: String = UUID.randomUUID().toString()
    val occurredOn: Instant = Instant.now()
}