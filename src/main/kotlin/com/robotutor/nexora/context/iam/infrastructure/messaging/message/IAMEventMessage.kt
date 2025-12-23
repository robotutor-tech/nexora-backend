package com.robotutor.nexora.context.iam.infrastructure.messaging.message

import com.robotutor.nexora.context.iam.domain.vo.CredentialKind
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

sealed class IAMEventMessage(name: String) : EventMessage("iam.$name")

class AccountCreatedEventMessage(val accountId: String) : IAMEventMessage("account.registered")
class AccountActivatedEventMessage(val accountId: String) : IAMEventMessage("account.activated")
class CredentialUpdatedEventMessage(val accountId: String, val kind: CredentialKind) :
    IAMEventMessage("account.credential.updated")

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


data class PremisesOwnerRegistrationFailedEventMessage(val premisesId: String) :
    IAMEventMessage("premises.owner.registration.failed")

data class PremisesOwnerRegisteredEventMessage(val premisesId: String) :
    IAMEventMessage("premises.owner.registered")
