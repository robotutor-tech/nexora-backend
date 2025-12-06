package com.robotutor.nexora.context.iam.infrastructure.messaging.message

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

sealed class IAMEventMessage(name: String) : EventMessage("iam.$name")

class AccountCreatedEventMessage(val accountId: String, val credentialId: String, type: String) :
    IAMEventMessage("account.created.${type}")

data class InvitationAcceptedEventMessage(val invitationId: String) : IAMEventMessage("invitation.accepted")
