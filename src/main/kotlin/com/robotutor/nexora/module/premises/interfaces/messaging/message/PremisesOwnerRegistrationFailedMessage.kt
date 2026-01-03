package com.robotutor.nexora.module.premises.interfaces.messaging.message

import com.robotutor.nexora.common.message.message.EventMessage

data class PremisesOwnerRegistrationFailedMessage(val premisesId: String) : EventMessage()
