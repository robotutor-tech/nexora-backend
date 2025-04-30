package com.robotutor.nexora.security.gateway.view

import com.robotutor.nexora.premises.models.PremisesId

data class AuthenticationResponseData(
    val identifier: String,
    val identifierType: IdentifierType,
    val premisesId: PremisesId? = null,
    val actor: ActorData? = null
)

data class ActorData(
    val type: ActorType,
    val identifier: String,
)

enum class ActorType {
    HUMAN,
    DEVICE,
    LOCAL_SERVER,
    SERVER
}

enum class IdentifierType {
    AUTH_USER,
    PREMISES_ACTOR,
    INVITATION;
}
