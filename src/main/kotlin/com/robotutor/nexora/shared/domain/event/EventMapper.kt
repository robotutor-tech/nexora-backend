package com.robotutor.nexora.shared.domain.event

interface EventMapper {
    fun toEventMessage(event: DomainEvent): EventMessage
}