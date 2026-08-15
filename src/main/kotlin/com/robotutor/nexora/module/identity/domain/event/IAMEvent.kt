package com.robotutor.nexora.module.identity.domain.event

import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.domain.vo.*

sealed interface IdentityEvent : Event

data class AccountCreatedEvent(val accountId: AccountId, val type: AccountType, val subjectId: SubjectId) :
    IdentityEvent

data class AccountRegistrationFailedEvent(val type: AccountType, val subjectId: SubjectId) : IdentityEvent
data class CredentialUpdatedEvent(val accountId: AccountId) : IdentityEvent
data class AccountAuthenticatedEvent(val accountId: AccountId, val type: AccountType, val subjectId: SubjectId) :
    IdentityEvent

data class PremisesOwnerRegisteredEvent(val premisesId: PremisesId) : IdentityEvent
data class PremisesOwnerRegistrationFailedEvent(val premisesId: PremisesId) : IdentityEvent
data class ActorAuthenticatedEvent(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val accountId: AccountId,
    val type: AccountType
) : IdentityEvent

