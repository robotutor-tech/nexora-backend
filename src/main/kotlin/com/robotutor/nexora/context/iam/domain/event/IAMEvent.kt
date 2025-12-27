package com.robotutor.nexora.context.iam.domain.event

import com.robotutor.nexora.context.iam.domain.vo.CredentialKind
import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.principal.AccountType
import com.robotutor.nexora.shared.domain.vo.principal.PrincipalId

sealed interface IAMEvent : Event

data class AccountCreatedEvent(val accountId: AccountId, val type: AccountType, val principalId: PrincipalId) : IAMEvent
data class AccountRegistrationFailedEvent(val type: AccountType, val principalId: PrincipalId) : IAMEvent
data class CredentialUpdatedEvent(val accountId: AccountId, val kind: CredentialKind) : IAMEvent
data class AccountAuthenticatedEvent(val accountId: AccountId, val type: AccountType, val principalId: PrincipalId) :
    IAMEvent

data class PremisesOwnerRegisteredEvent(val premisesId: PremisesId) : IAMEvent
data class PremisesOwnerRegistrationFailedEvent(val premisesId: PremisesId) : IAMEvent
data class ActorAuthenticatedEvent(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val accountId: AccountId,
    val type: AccountType
) : IAMEvent

