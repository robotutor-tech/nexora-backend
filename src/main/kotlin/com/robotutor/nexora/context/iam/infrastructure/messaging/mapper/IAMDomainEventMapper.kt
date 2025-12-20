package com.robotutor.nexora.context.iam.infrastructure.messaging.mapper

import com.robotutor.nexora.context.iam.domain.event.AccountActivatedEvent
import com.robotutor.nexora.context.iam.domain.event.AccountCreatedEvent
import com.robotutor.nexora.context.iam.domain.event.IAMDomainEvent
import com.robotutor.nexora.context.iam.domain.event.InvitationAcceptedEvent
import com.robotutor.nexora.context.iam.domain.vo.CredentialKind
import com.robotutor.nexora.context.iam.infrastructure.messaging.message.AccountActivatedEventMessage
import com.robotutor.nexora.context.iam.infrastructure.messaging.message.AccountCreatedEventMessage
import com.robotutor.nexora.context.iam.infrastructure.messaging.message.InvitationAcceptedEventMessage
import com.robotutor.nexora.context.iam.interfaces.messaging.message.AccountActivatedMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.domain.vo.AccountType
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

object IAMDomainEventMapper : EventMapper<IAMDomainEvent> {
    override fun toEventMessage(event: IAMDomainEvent): EventMessage {
        return when (event) {
            is InvitationAcceptedEvent -> toInvitationAcceptedEventMessage(event)
            is AccountCreatedEvent -> toAccountCreatedEventMessage(event)
            is AccountActivatedEvent -> toAccountActivatedEventMessage(event)
        }
    }

    private fun toInvitationAcceptedEventMessage(event: InvitationAcceptedEvent): InvitationAcceptedEventMessage {
        return InvitationAcceptedEventMessage(event.invitationId.value)
    }

    private fun toAccountCreatedEventMessage(event: AccountCreatedEvent): AccountCreatedEventMessage {
        return AccountCreatedEventMessage(event.accountId.value)
    }

    private fun toAccountActivatedEventMessage(event: AccountActivatedEvent): AccountActivatedEventMessage {
        return AccountActivatedEventMessage(event.accountId.value)
    }
}