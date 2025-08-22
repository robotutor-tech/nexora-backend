package com.robotutor.nexora.modules.premises.interfaces.controller.dto

import com.robotutor.nexora.shared.domain.model.RoleType
import java.time.Instant

data class PremisesResponse(
    val premisesId: String,
    val name: String,
    val createdAt: Instant
)

data class PremisesActorResponse(
    val premisesId: String,
    val name: String,
    val createdAt: Instant,
    val actor: ActorWithRoleResponse
)

data class ActorWithRoleResponse(
    val actorId: String,
    val premisesId: String,
    val roles: List<RoleResponse>,
)

data class RoleResponse(
    val roleId: String,
    val name: String,
    val roleType: RoleType,
)
