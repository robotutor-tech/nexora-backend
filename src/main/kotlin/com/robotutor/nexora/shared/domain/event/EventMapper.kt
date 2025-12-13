package com.robotutor.nexora.shared.domain.event

import com.robotutor.nexora.shared.domain.BusinessEvent
import com.robotutor.nexora.shared.domain.DomainEvent
import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

interface EventMapper<T : Event> {
    fun toEventMessage(event: T): EventMessage
}

interface DomainEventMapper<T : DomainEvent> : EventMapper<T>
interface BusinessEventMapper<T : BusinessEvent> : EventMapper<T>