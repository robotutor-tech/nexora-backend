package com.robotutor.nexora.context.premises.interfaces.messaging.message

import com.robotutor.nexora.common.messaging.message.EventMessage

data class PremisesOwnerRegistrationFailedMessage(val premisesId: String) : EventMessage()
