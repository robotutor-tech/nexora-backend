package com.robotutor.nexora.shared.domain.event

import com.robotutor.nexora.shared.domain.DomainEvent
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

interface EventMapper<T : DomainEvent> {
    fun toEventMessage(event: T): EventMessage
}