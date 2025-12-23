package com.robotutor.nexora.context.iam.infrastructure.messaging.mapper

import com.robotutor.nexora.context.iam.domain.event.*
import com.robotutor.nexora.context.iam.infrastructure.messaging.message.*
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

object IAMEventMapper : EventMapper<IAMEvent> {
    override fun toEventMessage(event: IAMEvent): EventMessage {
        return when (event) {
            is AccountAuthenticatedEvent -> toAccountAuthenticatedEventMessage(event)
            is ActorAuthenticatedEvent -> toActorAuthenticatedEventMessage(event)
            is AccountCompensatedEvent -> toAccountCompensatedEventMessage(event)
            is PremisesOwnerRegistrationFailedEvent -> toPremisesOwnerRegistrationFailedEventMessage(event)
            is AccountCreatedEvent -> toAccountCreatedEventMessage(event)
            is AccountActivatedEvent -> toAccountActivatedEventMessage(event)
            is CredentialUpdatedEvent -> toCredentialUpdatedEventMessage(event)
            is PremisesOwnerRegisteredEvent -> toPremisesOwnerRegisteredEventMessage(event)
        }
    }

    private fun toCredentialUpdatedEventMessage(event: CredentialUpdatedEvent): CredentialUpdatedEventMessage {
        return CredentialUpdatedEventMessage(event.accountId.value, event.kind)
    }

    private fun toAccountCreatedEventMessage(event: AccountCreatedEvent): AccountCreatedEventMessage {
        return AccountCreatedEventMessage(event.accountId.value)
    }

    private fun toAccountActivatedEventMessage(event: AccountActivatedEvent): AccountActivatedEventMessage {
        return AccountActivatedEventMessage(event.accountId.value)
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

    private fun toAccountCompensatedEventMessage(event: AccountCompensatedEvent): AccountCompensatedEventMessage {
        return AccountCompensatedEventMessage(event.accountId.value)
    }
}