package com.robotutor.nexora.context.iam.infrastructure.messaging.message

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

sealed class IAMEventMessage(name: String) : EventMessage("iam.$name")

class AccountCreatedEventMessage(val accountId: String) : IAMEventMessage("account.registered")
class AccountActivatedEventMessage(val accountId: String) : IAMEventMessage("account.activated")

class AccountAuthenticatedEventMessage(val accountId: String, val type: String) :
    IAMEventMessage("account.authenticated")

class AccountCompensatedEventMessage(val accountId: String) : IAMEventMessage("account.compensated")

class ActorAuthenticatedEventMessage(
    val accountId: String,
    val type: String,
    val actorId: String,
    val premisesId: String
) :
    IAMEventMessage("actor.authenticated")


data class InvitationAcceptedEventMessage(val invitationId: String) : IAMEventMessage("invitation.accepted")
data class PremisesResourceCreatedEventMessage(val premisesId: String, val ownerId: String) : IAMEventMessage("premises.resource.created")
