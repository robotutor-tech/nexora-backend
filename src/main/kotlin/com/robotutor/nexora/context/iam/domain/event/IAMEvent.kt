package com.robotutor.nexora.context.iam.domain.event

import com.robotutor.nexora.context.iam.domain.vo.CredentialKind
import com.robotutor.nexora.context.iam.domain.vo.OwnerId
import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.AccountType
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId

sealed interface IAMEvent : Event

data class AccountCreatedEvent(val accountId: AccountId, val type: AccountType, val ownerId: OwnerId) : IAMEvent
data class AccountRegistrationFailedEvent(val type: AccountType, val ownerId: OwnerId) : IAMEvent
data class CredentialUpdatedEvent(val accountId: AccountId, val kind: CredentialKind) : IAMEvent
data class AccountAuthenticatedEvent(val accountId: AccountId, val type: AccountType) : IAMEvent
data class PremisesOwnerRegisteredEvent(val premisesId: PremisesId) : IAMEvent
data class PremisesOwnerRegistrationFailedEvent(val premisesId: PremisesId) : IAMEvent
data class ActorAuthenticatedEvent(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val accountId: AccountId,
    val type: AccountType
) : IAMEvent

