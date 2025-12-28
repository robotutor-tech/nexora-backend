package com.robotutor.nexora.shared.domain.event

import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.common.messaging.infrastructure.message.EventMessage

interface EventMapper<T : Event> {
    fun toEventMessage(event: T): EventMessage
}
