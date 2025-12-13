package com.robotutor.nexora.context.user.infrastructure.messaging.mapper

import com.robotutor.nexora.context.user.domain.event.UserBusinessEvent
import com.robotutor.nexora.context.user.domain.event.UserRegistrationCompensatedEvent
import com.robotutor.nexora.context.user.infrastructure.messaging.message.UserRegistrationCompensatedEventMessage
import com.robotutor.nexora.shared.domain.event.BusinessEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

object UserBusinessEventMapper : BusinessEventMapper<UserBusinessEvent> {
    override fun toEventMessage(event: UserBusinessEvent): EventMessage {
        return when (event) {
            is UserRegistrationCompensatedEvent -> toUserRegistrationCompensatedEventMessage(event)
        }
    }

    private fun toUserRegistrationCompensatedEventMessage(event: UserRegistrationCompensatedEvent): UserRegistrationCompensatedEventMessage {
        return UserRegistrationCompensatedEventMessage(event.userId.value)
    }
}