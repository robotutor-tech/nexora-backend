package com.robotutor.nexora.context.premises.interfaces.messaging.message

import com.robotutor.nexora.common.messaging.infrastructure.message.EventMessage

data class PremisesOwnerRegisteredMessage(val premisesId: String) : EventMessage()
