package com.robotutor.nexora.shared.message.message

import com.robotutor.nexora.shared.message.config.EventName

interface EventMessage {
    val eventName: EventName
}

