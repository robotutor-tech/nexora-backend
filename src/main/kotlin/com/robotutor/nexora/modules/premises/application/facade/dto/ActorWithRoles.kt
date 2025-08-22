package com.robotutor.nexora.modules.premises.application.facade.dto

import com.robotutor.nexora.shared.domain.model.*

data class ActorWithRoles(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val roles: List<Role>,
    val principalType: ActorPrincipalType,
    val principal: PrincipalContext,
)

data class Role(
    val roleId: RoleId,
    val premisesId: PremisesId,
    val name: String,
    val roleType: RoleType,
)