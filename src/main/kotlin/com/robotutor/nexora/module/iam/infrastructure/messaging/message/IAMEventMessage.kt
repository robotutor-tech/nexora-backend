package com.robotutor.nexora.module.iam.infrastructure.messaging.message

import com.robotutor.nexora.module.iam.domain.vo.CredentialKind
import com.robotutor.nexora.common.message.message.EventMessage

sealed class IAMEventMessage(name: String) : EventMessage("iam.$name")

class CredentialUpdatedEventMessage(val accountId: String, val kind: CredentialKind) :
    IAMEventMessage("account.credential.updated")

class AccountAuthenticatedEventMessage(val accountId: String, val type: String) :
    IAMEventMessage("account.authenticated")

class ActorAuthenticatedEventMessage(
    val accountId: String,
    val type: String,
    val actorId: String,
    val premisesId: String
) : IAMEventMessage("actor.authenticated")


data class PremisesOwnerRegistrationFailedEventMessage(val premisesId: String) :
    IAMEventMessage("premises.owner.registration.failed")

data class PremisesOwnerRegisteredEventMessage(val premisesId: String) :
    IAMEventMessage("premises.owner.registered")
