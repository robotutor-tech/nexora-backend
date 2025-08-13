package com.robotutor.nexora.shared.domain.event

interface EventPublisher {
    fun publish(event: DomainEvent): DomainEvent
}