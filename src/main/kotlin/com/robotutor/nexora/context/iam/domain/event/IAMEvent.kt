package com.robotutor.nexora.context.iam.domain.event

import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.context.user.domain.vo.UserId
import com.robotutor.nexora.shared.domain.DomainEvent
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.InvitationId

sealed interface IAMEvent : DomainEvent

data class AccountCreatedEvent(val account: AccountAggregate) : IAMEvent
data class IAMUserRegisteredEvent(val userId: UserId) : IAMEvent
data class IAMDeviceRegisteredEvent(val deviceId: DeviceId) : IAMEvent
data class InvitationAcceptedEvent(val invitationId: InvitationId) : IAMEvent