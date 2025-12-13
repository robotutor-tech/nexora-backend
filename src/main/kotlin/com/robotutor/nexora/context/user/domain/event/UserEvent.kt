package com.robotutor.nexora.context.user.domain.event

import com.robotutor.nexora.context.user.domain.vo.UserId
import com.robotutor.nexora.shared.domain.BusinessEvent
import com.robotutor.nexora.shared.domain.DomainEvent
import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.domain.vo.AccountId

sealed interface UserEvent : Event

sealed interface UserDomainEvent : DomainEvent, UserEvent
data class UserRegisteredEvent(val userId: UserId) : UserDomainEvent
data class UserActivatedEvent(val userId: UserId, val accountId: AccountId) : UserDomainEvent

sealed interface UserBusinessEvent : BusinessEvent, UserEvent
data class UserRegistrationCompensatedEvent(val userId: UserId) : UserBusinessEvent
