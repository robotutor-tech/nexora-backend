package com.robotutor.nexora.modules.premises.infrastructure.messaging.message

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

data class PremisesCreatedEventMessage(val premisesId: String, val name: String) : EventMessage("premises.created")
