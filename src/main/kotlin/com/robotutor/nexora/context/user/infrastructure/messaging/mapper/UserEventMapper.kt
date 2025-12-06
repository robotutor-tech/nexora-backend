package com.robotutor.nexora.context.user.infrastructure.messaging.mapper

import com.robotutor.nexora.context.user.domain.event.UserActivatedEvent
import com.robotutor.nexora.context.user.domain.event.UserEvent
import com.robotutor.nexora.context.user.domain.event.UserRegisteredEvent
import com.robotutor.nexora.context.user.domain.event.UserRegistrationCompensatedEvent
import com.robotutor.nexora.context.user.infrastructure.messaging.message.UserActivatedEventMessage
import com.robotutor.nexora.context.user.infrastructure.messaging.message.UserRegisteredEventMessage
import com.robotutor.nexora.context.user.infrastructure.messaging.message.UserRegistrationCompensatedEventMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

object UserEventMapper : EventMapper<UserEvent> {
    override fun toEventMessage(event: UserEvent): EventMessage {
        return when (event) {
            is UserRegisteredEvent -> toUserRegisteredEventMessage(event)
            is UserActivatedEvent -> toUserActivatedEventMessage(event)
            is UserRegistrationCompensatedEvent -> toUserRegistrationCompensatedEventMessage(event)
        }
    }

    private fun toUserRegistrationCompensatedEventMessage(event: UserRegistrationCompensatedEvent): UserRegistrationCompensatedEventMessage {
        return UserRegistrationCompensatedEventMessage(event.userId.value)
    }

    private fun toUserRegisteredEventMessage(event: UserRegisteredEvent): UserRegisteredEventMessage {
        return UserRegisteredEventMessage(event.userId.value)
    }

    private fun toUserActivatedEventMessage(event: UserActivatedEvent): UserActivatedEventMessage {
        return UserActivatedEventMessage(userId = event.userId.value, accountId = event.accountId.value)
    }
}