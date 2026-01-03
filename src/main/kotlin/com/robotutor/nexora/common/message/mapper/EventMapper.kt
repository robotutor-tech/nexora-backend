package com.robotutor.nexora.common.message.mapper

import com.robotutor.nexora.common.message.message.EventMessage
import com.robotutor.nexora.shared.domain.Event

interface EventMapper<T : Event> {
    fun toEventMessage(event: T): EventMessage
}