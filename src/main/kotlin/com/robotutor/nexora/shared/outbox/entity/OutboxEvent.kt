package com.robotutor.nexora.shared.outbox.entity

import com.robotutor.nexora.shared.message.message.EventMessage

data class OutboxEvent(
    val message: EventMessage,
)
