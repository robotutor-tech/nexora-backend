package com.robotutor.nexora.context.iam.infrastructure.messaging.mapper

import com.robotutor.nexora.context.iam.domain.event.*
import com.robotutor.nexora.context.iam.infrastructure.messaging.message.*
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.domain.vo.AccountType
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

object IAMEventMapper : EventMapper<IAMEvent> {
    override fun toEventMessage(event: IAMEvent): EventMessage {
        return when (event) {
            is AccountCreatedEvent -> toAccountCreatedEventMessage(event)
            is AccountRegistrationFailedEvent -> toAccountRegistrationFailedEventMessage(event)
            is AccountAuthenticatedEvent -> toAccountAuthenticatedEventMessage(event)
            is ActorAuthenticatedEvent -> toActorAuthenticatedEventMessage(event)
            is PremisesOwnerRegistrationFailedEvent -> toPremisesOwnerRegistrationFailedEventMessage(event)
            is CredentialUpdatedEvent -> toCredentialUpdatedEventMessage(event)
            is PremisesOwnerRegisteredEvent -> toPremisesOwnerRegisteredEventMessage(event)
        }
    }

    private fun toCredentialUpdatedEventMessage(event: CredentialUpdatedEvent): CredentialUpdatedEventMessage {
        return CredentialUpdatedEventMessage(event.accountId.value, event.kind)
    }

    private fun toAccountCreatedEventMessage(event: AccountCreatedEvent): AccountCreatedEventMessage {
        return when (event.type) {
            AccountType.HUMAN -> UserAccountCreatedEventMessage(event.ownerId.value, event.accountId)
            AccountType.MACHINE -> DeviceAccountCreatedEventMessage(event.ownerId.value, event.accountId)
        }
    }

    private fun toAccountRegistrationFailedEventMessage(event: AccountRegistrationFailedEvent): AccountRegistrationFailedEventMessage {
        return when (event.type) {
            AccountType.HUMAN -> UserAccountRegistrationFailedEventMessage(event.ownerId.value)
            AccountType.MACHINE -> DeviceAccountRegistrationFailedEventMessage(event.ownerId.value)
        }
    }

    private fun toPremisesOwnerRegisteredEventMessage(event: PremisesOwnerRegisteredEvent): PremisesOwnerRegisteredEventMessage {
        return PremisesOwnerRegisteredEventMessage(event.premisesId.value)
    }

    private fun toPremisesOwnerRegistrationFailedEventMessage(event: PremisesOwnerRegistrationFailedEvent): PremisesOwnerRegistrationFailedEventMessage {
        return PremisesOwnerRegistrationFailedEventMessage(event.premisesId.value)
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
}