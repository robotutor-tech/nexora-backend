package com.robotutor.nexora.module.user.domain.event

import com.robotutor.nexora.module.user.domain.vo.UserId
import com.robotutor.nexora.shared.domain.Event

sealed interface UserEvent : Event

data class UserRegisteredEvent(val userId: UserId) : UserEvent
data class UserActivatedEvent(val userId: UserId) : UserEvent
data class UserCompensatedEvent(val userId: UserId) : UserEvent

