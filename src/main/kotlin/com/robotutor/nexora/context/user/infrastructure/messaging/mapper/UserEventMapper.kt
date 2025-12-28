package com.robotutor.nexora.context.user.infrastructure.messaging.mapper

import com.robotutor.nexora.context.user.domain.event.UserActivatedEvent
import com.robotutor.nexora.context.user.domain.event.UserCompensatedEvent
import com.robotutor.nexora.context.user.domain.event.UserEvent
import com.robotutor.nexora.context.user.domain.event.UserRegisteredEvent
import com.robotutor.nexora.context.user.infrastructure.messaging.message.UserActivatedEventMessage
import com.robotutor.nexora.context.user.infrastructure.messaging.message.UserCompensatedEventMessage
import com.robotutor.nexora.context.user.infrastructure.messaging.message.UserRegisteredEventMessage
import com.robotutor.nexora.common.messaging.mapper.EventMapper
import com.robotutor.nexora.common.messaging.message.EventMessage

object UserEventMapper : EventMapper<UserEvent> {
    override fun toEventMessage(event: UserEvent): EventMessage {
        return when (event) {
            is UserRegisteredEvent -> UserRegisteredEventMessage(event.userId.value)
            is UserActivatedEvent -> UserActivatedEventMessage(event.userId.value)
            is UserCompensatedEvent -> UserCompensatedEventMessage(event.userId.value)
        }
    }
}