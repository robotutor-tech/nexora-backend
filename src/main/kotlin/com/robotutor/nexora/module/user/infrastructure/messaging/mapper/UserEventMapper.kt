package com.robotutor.nexora.module.user.infrastructure.messaging.mapper

import com.robotutor.nexora.module.user.domain.event.UserAccountCreationCompensatedEvent
import com.robotutor.nexora.module.user.domain.event.UserActivatedEvent
import com.robotutor.nexora.module.user.domain.event.UserEvent
import com.robotutor.nexora.module.user.domain.event.UserRegisteredEvent
import com.robotutor.nexora.module.user.infrastructure.messaging.message.UserAccountCreationCompensatedEventMessage
import com.robotutor.nexora.module.user.infrastructure.messaging.message.UserActivatedEventMessage
import com.robotutor.nexora.module.user.infrastructure.messaging.message.UserRegisteredEventMessage
import com.robotutor.nexora.shared.message.mapper.EventMapper
import com.robotutor.nexora.shared.message.message.EventMessage
import org.springframework.stereotype.Component

@Component
class UserEventMapper : EventMapper<UserEvent> {
    override fun toEventMessage(event: UserEvent): EventMessage {
        return when (event) {
            is UserRegisteredEvent -> UserRegisteredEventMessage(event.userId.value)
            is UserActivatedEvent -> UserActivatedEventMessage(event.userId.value)
            is UserAccountCreationCompensatedEvent -> UserAccountCreationCompensatedEventMessage(event.userId.value)
        }
    }
}
