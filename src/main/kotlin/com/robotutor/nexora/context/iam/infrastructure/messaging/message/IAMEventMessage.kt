package com.robotutor.nexora.context.iam.infrastructure.messaging.message

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

sealed class IAMEventMessage(name: String) : EventMessage("iam.$name")

class AccountCreatedEventMessage(val accountId: String, val credentialId: String, type: String) :
    IAMEventMessage("account.created.${type}")

class AccountAuthenticatedEventMessage(val accountId: String, val type: String) :
    IAMEventMessage("account.authenticated")

class ActorAuthenticatedEventMessage(
    val accountId: String,
    val type: String,
    val actorId: String,
    val premisesId: String
) :
    IAMEventMessage("actor.authenticated")


data class InvitationAcceptedEventMessage(val invitationId: String) : IAMEventMessage("invitation.accepted")
