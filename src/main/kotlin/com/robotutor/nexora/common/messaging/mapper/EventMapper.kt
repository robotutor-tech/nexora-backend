package com.robotutor.nexora.common.messaging.mapper

import com.robotutor.nexora.common.messaging.message.EventMessage
import com.robotutor.nexora.shared.domain.Event

interface EventMapper<T : Event> {
    fun toEventMessage(event: T): EventMessage
}