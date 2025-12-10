package com.robotutor.nexora.context.iam.domain.event

import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.context.iam.domain.aggregate.AccountType
import com.robotutor.nexora.context.user.domain.vo.UserId
import com.robotutor.nexora.shared.domain.DomainEvent
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.InvitationId
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId

sealed interface IAMEvent : DomainEvent

data class AccountCreatedEvent(val account: AccountAggregate) : IAMEvent
data class AccountAuthenticatedEvent(val accountId: AccountId, val type: AccountType) : IAMEvent
data class ActorAuthenticatedEvent(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val accountId: AccountId,
    val type: AccountType
) : IAMEvent


data class IAMUserRegisteredEvent(val userId: UserId) : IAMEvent
data class IAMDeviceRegisteredEvent(val deviceId: DeviceId) : IAMEvent
data class InvitationAcceptedEvent(val invitationId: InvitationId) : IAMEvent