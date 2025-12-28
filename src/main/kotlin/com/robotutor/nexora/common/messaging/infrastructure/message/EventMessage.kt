package com.robotutor.nexora.common.messaging.infrastructure.message

import java.time.Instant
import java.util.*

open class EventMessage(
    val eventName: String = "",
    val occurredOn: Instant = Instant.now(),
    val id: String = UUID.randomUUID().toString()
)