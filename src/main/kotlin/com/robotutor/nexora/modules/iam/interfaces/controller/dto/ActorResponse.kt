package com.robotutor.nexora.modules.iam.interfaces.controller.dto

import com.robotutor.nexora.modules.iam.domain.model.ActorState
import com.robotutor.nexora.shared.domain.model.ActorPrincipalType
import com.robotutor.nexora.shared.interfaces.dto.PrincipalContextResponse

data class ActorWithRolesResponse(
    val actorId: String,
    val premisesId: String,
    val principalType: ActorPrincipalType,
    val principal: PrincipalContextResponse,
    val roles: List<RoleResponse>,
    val state: ActorState,
)

data class ActorResponse(
    val actorId: String,
    val premisesId: String,
    val principalType: ActorPrincipalType,
    val principal: PrincipalContextResponse,
    val role: RoleResponse,
    val state: ActorState,
)

