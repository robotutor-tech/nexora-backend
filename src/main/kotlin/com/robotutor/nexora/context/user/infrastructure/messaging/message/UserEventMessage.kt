package com.robotutor.nexora.context.user.infrastructure.messaging.message

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

sealed class UserEventMessage(eventName: String) : EventMessage("user.$eventName")

data class UserRegisteredEventMessage(val userId: String) : UserEventMessage("user.registered")
data class UserActivatedEventMessage(val userId: String, val accountId: String) : UserEventMessage("user.activated")
data class UserRegistrationCompensatedEventMessage(val userId: String) :
    UserEventMessage("user.registration.compensated")