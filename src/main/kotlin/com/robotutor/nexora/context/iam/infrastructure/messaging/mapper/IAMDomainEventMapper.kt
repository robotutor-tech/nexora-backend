package com.robotutor.nexora.context.iam.infrastructure.messaging.mapper

import com.robotutor.nexora.context.iam.domain.event.*
import com.robotutor.nexora.context.iam.infrastructure.messaging.message.AccountActivatedEventMessage
import com.robotutor.nexora.context.iam.infrastructure.messaging.message.AccountCreatedEventMessage
import com.robotutor.nexora.context.iam.infrastructure.messaging.message.CredentialUpdatedEventMessage
import com.robotutor.nexora.context.iam.infrastructure.messaging.message.InvitationAcceptedEventMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

object IAMDomainEventMapper : EventMapper<IAMDomainEvent> {
    override fun toEventMessage(event: IAMDomainEvent): EventMessage {
        return when (event) {
            is InvitationAcceptedEvent -> toInvitationAcceptedEventMessage(event)
            is AccountCreatedEvent -> toAccountCreatedEventMessage(event)
            is AccountActivatedEvent -> toAccountActivatedEventMessage(event)
            is CredentialUpdatedEvent -> toCredentialUpdatedEventMessage(event)
        }
    }

    private fun toInvitationAcceptedEventMessage(event: InvitationAcceptedEvent): InvitationAcceptedEventMessage {
        return InvitationAcceptedEventMessage(event.invitationId.value)
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
}