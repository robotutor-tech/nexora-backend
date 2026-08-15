package com.robotutor.nexora.module.identity.infrastructure.messaging.message

import com.robotutor.nexora.shared.message.config.EventName
import com.robotutor.nexora.shared.message.message.EventMessage

sealed interface IdentityEventMessage : EventMessage

data class CredentialUpdatedEventMessage(val accountId: String) : IdentityEventMessage {
    override val eventName: EventName = EventName.IDENTITY_ACCOUNT_CREDENTIAL_UPDATED
}

data class AccountAuthenticatedEventMessage(val accountId: String, val type: String) : IdentityEventMessage {
    override val eventName: EventName = EventName.IDENTITY_ACCOUNT_AUTHENTICATED
}

class ActorAuthenticatedEventMessage(
    val accountId: String,
    val type: String,
    val actorId: String,
    val premisesId: String
) : IdentityEventMessage {
    override val eventName: EventName = EventName.IDENTITY_ACTOR_AUTHENTICATED
}


data class PremisesOwnerRegistrationFailedEventMessage(val premisesId: String) : IdentityEventMessage {
    override val eventName: EventName = EventName.PREMISES_OWNER_REGISTRATION_FAILED
}

data class PremisesOwnerRegisteredEventMessage(val premisesId: String) : IdentityEventMessage {
    override val eventName: EventName = EventName.PREMISES_OWNER_REGISTERED
}
