package com.robotutor.nexora.shared.domain.event

interface EventMapper<T: DomainEvent> {
    fun toEventMessage(event: T): EventMessage
}