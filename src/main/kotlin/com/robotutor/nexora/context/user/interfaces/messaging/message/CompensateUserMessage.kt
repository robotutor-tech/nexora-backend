package com.robotutor.nexora.context.user.interfaces.messaging.message

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

data class CompensateUserMessage(val userId: String): EventMessage()
