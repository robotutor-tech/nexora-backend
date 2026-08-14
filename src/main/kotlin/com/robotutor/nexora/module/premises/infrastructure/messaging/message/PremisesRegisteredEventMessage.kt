package com.robotutor.nexora.module.premises.infrastructure.messaging.message

import com.robotutor.nexora.shared.message.config.EventName
import com.robotutor.nexora.shared.message.message.EventMessage

sealed interface PremisesEventMessage : EventMessage

data class PremisesRegisteredEventMessage(
    val premisesId: String,
    val name: String,
) : PremisesEventMessage{
    override val eventName: EventName = EventName.PREMISES_REGISTERED
}

