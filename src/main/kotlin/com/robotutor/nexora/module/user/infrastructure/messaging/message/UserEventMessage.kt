package com.robotutor.nexora.module.user.infrastructure.messaging.message

import com.robotutor.nexora.common.messaging.message.EventMessage

sealed class UserEventMessage(eventName: String) : EventMessage("user.$eventName")

data class UserRegisteredEventMessage(val userId: String) : UserEventMessage("registered")
data class UserActivatedEventMessage(val userId: String) : UserEventMessage("activated")
data class UserCompensatedEventMessage(val userId: String) : UserEventMessage("compensated")
