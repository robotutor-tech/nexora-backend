package com.robotutor.nexora.module.identity.interfaces.controller.view

import com.robotutor.nexora.module.identity.domain.aggregate.ActorStatus
import java.time.Instant

data class ActorResponse(
    val actorId: String,
    val accountId: String,
    val premisesId: String,
    val status: ActorStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
)
