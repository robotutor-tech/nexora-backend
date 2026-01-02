package com.robotutor.nexora.module.iam.interfaces.controller.view

import com.robotutor.nexora.module.iam.domain.aggregate.ActorStatus
import java.time.Instant

data class ActorResponse(
    val actorId: String,
    val accountId: String,
    val premisesId: String,
    val status: ActorStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
)