package com.robotutor.nexora.module.user.infrastructure.messaging.message

import com.robotutor.nexora.shared.message.config.EventName
import com.robotutor.nexora.shared.message.message.EventMessage

sealed interface UserEventMessage : EventMessage

class UserRegisteredEventMessage(val userId: String) : UserEventMessage {
    override val eventName: EventName = EventName.USER_REGISTERED
}

data class UserActivatedEventMessage(val userId: String) : UserEventMessage {
    override val eventName: EventName = EventName.USER_ACTIVATED
}

data class UserCompensatedEventMessage(val userId: String) : UserEventMessage {
    override val eventName: EventName = EventName.USER_COMPENSATED
}
