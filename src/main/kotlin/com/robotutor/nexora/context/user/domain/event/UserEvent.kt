package com.robotutor.nexora.context.user.domain.event

import com.robotutor.nexora.context.user.domain.vo.UserId
import com.robotutor.nexora.shared.domain.DomainEvent
import com.robotutor.nexora.shared.domain.vo.AccountId

sealed interface UserEvent : DomainEvent

data class UserRegisteredEvent(val userId: UserId) : UserEvent
data class UserRegistrationCompensatedEvent(val userId: UserId) : UserEvent
data class UserActivatedEvent(val userId: UserId, val accountId: AccountId) : UserEvent
