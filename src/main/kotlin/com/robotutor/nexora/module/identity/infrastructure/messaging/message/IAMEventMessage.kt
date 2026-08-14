package com.robotutor.nexora.module.identity.infrastructure.messaging.message

import com.robotutor.nexora.module.identity.domain.vo.CredentialKind
import com.robotutor.nexora.shared.message.config.EventName
import com.robotutor.nexora.shared.message.message.EventMessage

sealed interface IAMEventMessage : EventMessage

data class CredentialUpdatedEventMessage(val accountId: String, val kind: CredentialKind) : IAMEventMessage {
    override val eventName: EventName = EventName.IDENTITY_CREDENTIAL_UPDATED
}

data class AccountAuthenticatedEventMessage(val accountId: String, val type: String) : IAMEventMessage {
    override val eventName: EventName = EventName.ACCOUNT_AUTHENTICATED
}

class ActorAuthenticatedEventMessage(
    val accountId: String,
    val type: String,
    val actorId: String,
    val premisesId: String
) : IAMEventMessage {
    override val eventName: EventName = EventName.ACTOR_AUTHENTICATED
}


data class PremisesOwnerRegistrationFailedEventMessage(val premisesId: String) : IAMEventMessage {
    override val eventName: EventName = EventName.PREMISES_OWNER_REGISTRATION_FAILED
}

data class PremisesOwnerRegisteredEventMessage(val premisesId: String) : IAMEventMessage {
    override val eventName: EventName = EventName.PREMISES_OWNER_REGISTERED
}
