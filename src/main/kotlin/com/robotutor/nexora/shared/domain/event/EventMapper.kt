package com.robotutor.nexora.shared.domain.event

import com.robotutor.nexora.shared.domain.DomainEvent

interface EventMapper<T : DomainEvent> {
    fun toEventMessage(event: T): EventMessage
}