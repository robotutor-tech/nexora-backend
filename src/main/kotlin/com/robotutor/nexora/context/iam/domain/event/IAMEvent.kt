package com.robotutor.nexora.context.iam.domain.event

import com.robotutor.nexora.shared.domain.BusinessEvent
import com.robotutor.nexora.shared.domain.DomainEvent
import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.domain.model.InvitationId
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.AccountType
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId

sealed interface IAMEvent : Event

sealed interface IAMDomainEvent : DomainEvent, IAMEvent
data class AccountCreatedEvent(val accountId: AccountId) : IAMDomainEvent
data class AccountActivatedEvent(val accountId: AccountId) : IAMDomainEvent
data class InvitationAcceptedEvent(val invitationId: InvitationId) : IAMDomainEvent

sealed interface IAMBusinessEvent : BusinessEvent, IAMEvent
data class AccountAuthenticatedEvent(val accountId: AccountId, val type: AccountType) : IAMBusinessEvent
data class AccountCompensatedEvent(val accountId: AccountId) : IAMBusinessEvent
data class ActorAuthenticatedEvent(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val accountId: AccountId,
    val type: AccountType
) : IAMBusinessEvent
data class PremisesResourceCreatedEvent(val premisesId: PremisesId, val ownerAccountId: AccountId) : IAMBusinessEvent
