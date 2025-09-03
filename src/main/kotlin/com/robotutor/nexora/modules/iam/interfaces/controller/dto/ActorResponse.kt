package com.robotutor.nexora.modules.iam.interfaces.controller.dto

import com.robotutor.nexora.modules.iam.domain.entity.ActorState
import com.robotutor.nexora.shared.domain.model.ActorPrincipalType
import com.robotutor.nexora.shared.interfaces.dto.ActorPrincipalContextResponse

data class ActorWithRolesResponse(
    val actorId: String,
    val premisesId: String,
    val principalType: ActorPrincipalType,
    val principal: ActorPrincipalContextResponse,
    val roles: List<RoleResponse>,
    val state: ActorState,
)

data class ActorResponse(
    val actorId: String,
    val premisesId: String,
    val principalType: ActorPrincipalType,
    val principal: ActorPrincipalContextResponse,
    val role: RoleResponse,
)

