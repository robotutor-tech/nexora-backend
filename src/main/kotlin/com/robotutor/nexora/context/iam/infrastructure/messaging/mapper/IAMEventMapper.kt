package com.robotutor.nexora.context.iam.infrastructure.messaging.mapper

import com.robotutor.nexora.context.iam.domain.aggregate.AccountType
import com.robotutor.nexora.context.iam.domain.event.AccountAuthenticatedEvent
import com.robotutor.nexora.context.iam.domain.event.AccountCreatedEvent
import com.robotutor.nexora.context.iam.domain.event.ActorAuthenticatedEvent
import com.robotutor.nexora.context.iam.domain.event.IAMDeviceRegisteredEvent
import com.robotutor.nexora.context.iam.domain.event.IAMEvent
import com.robotutor.nexora.context.iam.domain.event.IAMUserRegisteredEvent
import com.robotutor.nexora.context.iam.domain.event.InvitationAcceptedEvent
import com.robotutor.nexora.context.iam.domain.vo.CredentialKind
import com.robotutor.nexora.context.iam.infrastructure.messaging.message.AccountAuthenticatedEventMessage
import com.robotutor.nexora.context.iam.infrastructure.messaging.message.AccountCreatedEventMessage
import com.robotutor.nexora.context.iam.infrastructure.messaging.message.ActorAuthenticatedEventMessage
import com.robotutor.nexora.context.iam.infrastructure.messaging.message.InvitationAcceptedEventMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

object IAMEventMapper : EventMapper<IAMEvent> {
    override fun toEventMessage(event: IAMEvent): EventMessage {
        return when (event) {
            is InvitationAcceptedEvent -> toInvitationAcceptedEventMessage(event)
            is IAMDeviceRegisteredEvent -> TODO()
            is IAMUserRegisteredEvent -> TODO()
            is AccountCreatedEvent -> toAccountCreatedEventMessage(event)
            is AccountAuthenticatedEvent -> toAccountAuthenticatedEventMessage(event)
            is ActorAuthenticatedEvent -> toActorAuthenticatedEventMessage(event)
        }
    }

    private fun toActorAuthenticatedEventMessage(event: ActorAuthenticatedEvent): ActorAuthenticatedEventMessage {
        return ActorAuthenticatedEventMessage(
            event.accountId.value,
            event.type.name,
            event.actorId.value,
            event.premisesId.value
        )
    }

    private fun toAccountAuthenticatedEventMessage(event: AccountAuthenticatedEvent): AccountAuthenticatedEventMessage {
        return AccountAuthenticatedEventMessage(event.accountId.value, event.type.name)
    }

    private fun toInvitationAcceptedEventMessage(event: InvitationAcceptedEvent): InvitationAcceptedEventMessage {
        return InvitationAcceptedEventMessage(event.invitationId.value)
    }

    private fun toAccountCreatedEventMessage(event: AccountCreatedEvent): AccountCreatedEventMessage {
        val credential = event.account.credentials.find { credential -> credential.kind == CredentialKind.PASSWORD }!!
        val type = if (event.account.type == AccountType.HUMAN) "human" else "machine"
        return AccountCreatedEventMessage(
            event.account.accountId.value,
            credential.credentialId.value,
            type
        )
    }

//    private fun toAuthUserRegisteredEventMessage(event: AuthUserRegisteredEvent): AuthUserRegisteredEventMessage {
//        return AuthUserRegisteredEventMessage(event.userId.value)
//    }
//
//    private fun toAuthDeviceRegisteredEventMessage(event: AuthDeviceRegisteredEvent): AuthDeviceRegisteredEventMessage {
//        return AuthDeviceRegisteredEventMessage(event.deviceId.value)
//    }
}