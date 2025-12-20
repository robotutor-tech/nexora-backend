package com.robotutor.nexora.context.user.infrastructure.messaging.mapper

import com.robotutor.nexora.context.user.domain.event.UserDomainEvent
import com.robotutor.nexora.context.user.domain.event.UserRegisteredEvent
import com.robotutor.nexora.context.user.infrastructure.messaging.message.UserRegisteredEventMessage
import com.robotutor.nexora.shared.domain.event.DomainEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

object UserDomainEventMapper : DomainEventMapper<UserDomainEvent> {
    override fun toEventMessage(event: UserDomainEvent): EventMessage {
        return when (event) {
            is UserRegisteredEvent -> toUserRegisteredEventMessage(event)
        }
    }

    private fun toUserRegisteredEventMessage(event: UserRegisteredEvent): UserRegisteredEventMessage {
        return UserRegisteredEventMessage(event.userId.value, event.accountId.value)
    }
}