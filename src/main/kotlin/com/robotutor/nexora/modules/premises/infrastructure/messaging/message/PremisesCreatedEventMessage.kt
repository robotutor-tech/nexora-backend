package com.robotutor.nexora.modules.premises.infrastructure.messaging.message

import com.robotutor.nexora.shared.domain.event.EventMessage

data class PremisesCreatedEventMessage(val premisesId: String, val name: String) : EventMessage
