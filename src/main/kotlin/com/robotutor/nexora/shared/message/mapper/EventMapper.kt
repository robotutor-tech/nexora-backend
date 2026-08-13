package com.robotutor.nexora.shared.message.mapper

import com.robotutor.nexora.shared.message.message.EventMessage
import com.robotutor.nexora.shared.domain.Event
import reactor.core.publisher.Mono

interface EventMapper<T : Event> {
    fun toEventMessage(event: T): EventMessage
}
