package com.robotutor.nexora.context.user.interfaces.messaging.message

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

data class CompensateUserRegistrationMessage(val userId: String) : EventMessage()

