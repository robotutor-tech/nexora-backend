package com.robotutor.nexora.context.user.interfaces.messaging.message

import com.robotutor.nexora.common.messaging.infrastructure.message.EventMessage

data class CompensateUserMessage(val userId: String): EventMessage()
