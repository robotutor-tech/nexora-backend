package com.robotutor.nexora.shared.message.message

import java.time.Instant
import java.util.*

open class EventMessage(
    val eventName: String = "",
    val occurredOn: Instant = Instant.now(),
//    val userId: String
//    val eventId: String
//    val correlationId: String
//    val occurredAt: Instant
    val id: String = UUID.randomUUID().toString()
)
