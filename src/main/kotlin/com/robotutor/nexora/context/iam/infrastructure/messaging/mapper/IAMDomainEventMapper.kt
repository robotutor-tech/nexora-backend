package com.robotutor.nexora.context.iam.infrastructure.messaging.mapper

import com.robotutor.nexora.context.iam.domain.event.AccountCreatedEvent
import com.robotutor.nexora.context.iam.domain.event.IAMDomainEvent
import com.robotutor.nexora.context.iam.domain.event.InvitationAcceptedEvent
import com.robotutor.nexora.context.iam.domain.vo.CredentialKind
import com.robotutor.nexora.context.iam.infrastructure.messaging.message.AccountCreatedEventMessage
import com.robotutor.nexora.context.iam.infrastructure.messaging.message.InvitationAcceptedEventMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.domain.vo.AccountType
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

object IAMDomainEventMapper : EventMapper<IAMDomainEvent> {
    override fun toEventMessage(event: IAMDomainEvent): EventMessage {
        return when (event) {
            is InvitationAcceptedEvent -> toInvitationAcceptedEventMessage(event)
            is AccountCreatedEvent -> toAccountCreatedEventMessage(event)
        }
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
}