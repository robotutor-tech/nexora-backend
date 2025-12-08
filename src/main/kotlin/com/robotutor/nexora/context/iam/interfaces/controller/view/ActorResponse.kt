package com.robotutor.nexora.context.iam.interfaces.controller.view

import com.robotutor.nexora.context.iam.domain.aggregate.ActorStatus
import java.time.Instant

data class ActorResponse(
    val actorId: String,
    val accountId: String,
    val premisesId: String,
    val status: ActorStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
)