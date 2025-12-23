package com.robotutor.nexora.context.user.domain.event

import com.robotutor.nexora.context.user.domain.vo.UserId
import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.domain.vo.AccountId

sealed interface UserEvent : Event

data class UserRegisteredEvent(val userId: UserId, val accountId: AccountId) : UserEvent
data class UserRegistrationFailedEvent(val accountId: AccountId) : UserEvent

