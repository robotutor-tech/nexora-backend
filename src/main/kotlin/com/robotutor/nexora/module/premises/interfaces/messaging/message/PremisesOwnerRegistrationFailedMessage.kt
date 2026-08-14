package com.robotutor.nexora.module.premises.interfaces.messaging.message

import com.robotutor.nexora.shared.message.config.EventName
import com.robotutor.nexora.shared.message.message.EventMessage

data class PremisesOwnerRegistrationFailedMessage(val premisesId: String) : EventMessage{
    override val eventName: EventName = EventName.PREMISES_OWNER_REGISTRATION_FAILED
}
