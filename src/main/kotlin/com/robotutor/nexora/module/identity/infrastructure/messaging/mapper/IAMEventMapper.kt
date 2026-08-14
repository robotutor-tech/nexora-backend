package com.robotutor.nexora.module.identity.infrastructure.messaging.mapper

import com.robotutor.nexora.module.identity.domain.event.*
import com.robotutor.nexora.module.identity.infrastructure.messaging.message.*
import com.robotutor.nexora.shared.message.mapper.EventMapper
import com.robotutor.nexora.shared.domain.vo.principal.SubjectType
import com.robotutor.nexora.shared.message.message.EventMessage

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
            SubjectType.USER -> UserAccountCreatedEventMessage(event.subjectId.value, event.accountId)
            SubjectType.DEVICE -> DeviceAccountCreatedEventMessage(event.subjectId.value, event.accountId)
        }
    }

    private fun toAccountRegistrationFailedEventMessage(event: AccountRegistrationFailedEvent): AccountRegistrationFailedEventMessage {
        return when (event.type) {
            SubjectType.USER -> UserAccountRegistrationFailedEventMessage(event.subjectId.value)
            SubjectType.DEVICE -> DeviceAccountRegistrationFailedEventMessage(event.subjectId.value)
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
