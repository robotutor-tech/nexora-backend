package com.robotutor.nexora.security.models

import com.robotutor.nexora.premises.models.PremisesId

data class ActorData(
    val actorIdentifier: Identifier<ActorIdentifier>,
    val actorId: ActorId,
    val premisesId: PremisesId
)