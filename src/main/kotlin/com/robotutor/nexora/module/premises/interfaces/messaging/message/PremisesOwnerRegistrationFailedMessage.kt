package com.robotutor.nexora.module.premises.interfaces.messaging.message

import com.robotutor.nexora.shared.message.message.EventMessage

data class PremisesOwnerRegistrationFailedMessage(val premisesId: String) : EventMessage()
