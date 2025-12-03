package com.robotutor.nexora.shared.domain

import java.time.Instant
import java.util.UUID

open class DomainEvent(name: String) {
    val id: String = UUID.randomUUID().toString()
    val occurredOn: Instant = Instant.now()
    val eventName: String = name
}