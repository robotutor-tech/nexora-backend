package com.robotutor.nexora.context.user.infrastructure.messaging.message

import com.robotutor.nexora.shared.domain.event.EventMessage

data class UserRegisteredEventMessage(val userId: String) : EventMessage