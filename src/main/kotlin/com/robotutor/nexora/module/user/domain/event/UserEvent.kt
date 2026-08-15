package com.robotutor.nexora.module.user.domain.event

import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.domain.vo.UserId

sealed interface UserEvent : Event

data class UserRegisteredEvent(val userId: UserId) : UserEvent
data class UserActivatedEvent(val userId: UserId) : UserEvent
data class UserCompensatedEvent(val userId: UserId) : UserEvent
