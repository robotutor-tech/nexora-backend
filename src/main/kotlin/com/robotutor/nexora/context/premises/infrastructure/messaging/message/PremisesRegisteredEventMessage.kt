package com.robotutor.nexora.context.premises.infrastructure.messaging.message

import com.robotutor.nexora.common.messaging.message.EventMessage

sealed class PremisesEventMessage(eventName: String) : EventMessage("premises.$eventName")

data class PremisesRegisteredEventMessage(
    val premisesId: String,
    val name: String,
) : PremisesEventMessage("registered")

