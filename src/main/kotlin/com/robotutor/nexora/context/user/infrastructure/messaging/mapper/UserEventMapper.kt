package com.robotutor.nexora.context.user.infrastructure.messaging.mapper

import com.robotutor.nexora.context.user.domain.event.UserEvent
import com.robotutor.nexora.context.user.domain.event.UserRegisteredEvent
import com.robotutor.nexora.context.user.domain.event.UserRegistrationFailedEvent
import com.robotutor.nexora.context.user.infrastructure.messaging.message.UserRegisteredEventMessage
import com.robotutor.nexora.context.user.infrastructure.messaging.message.UserRegistrationFailedEventMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

object UserEventMapper : EventMapper<UserEvent> {
    override fun toEventMessage(event: UserEvent): EventMessage {
        return when (event) {
            is UserRegisteredEvent -> toUserRegisteredEventMessage(event)
            is UserRegistrationFailedEvent -> toUserRegistrationFailedEventMessage(event)
        }
    }

    private fun toUserRegistrationFailedEventMessage(event: UserRegistrationFailedEvent): UserRegistrationFailedEventMessage {
        return UserRegistrationFailedEventMessage(event.accountId.value)
    }

    private fun toUserRegisteredEventMessage(event: UserRegisteredEvent): UserRegisteredEventMessage {
        return UserRegisteredEventMessage(event.userId.value, event.accountId.value)
    }
}