package com.robotutor.nexora.modules.user.infrastructure.messaging.mapper

import com.robotutor.nexora.modules.user.domain.event.UserEvent
import com.robotutor.nexora.modules.user.domain.event.UserRegisteredEvent
import com.robotutor.nexora.modules.user.infrastructure.messaging.message.UserRegisteredEventMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.domain.event.EventMessage
import org.springframework.stereotype.Service

@Service
class UserEventMapper : EventMapper<UserEvent> {
    override fun toEventMessage(event: UserEvent): EventMessage {
        return when (event) {
            is UserRegisteredEvent -> toUserRegisteredEventMessage(event)
        }
    }

    private fun toUserRegisteredEventMessage(event: UserRegisteredEvent): UserRegisteredEventMessage {
        return UserRegisteredEventMessage(event.userId.value)
    }
}