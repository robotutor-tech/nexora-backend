package com.robotutor.nexora.shared.domain.model

import java.time.Instant

sealed class PrincipalData

data class UserData(
    val userId: UserId,
    val name: String,
    val email: Email,
    val registeredAt: Instant
) : PrincipalData()

data class ActorData(
    val actorId: ActorId,
    val role: Role,
    val premisesId: PremisesId,
    val principalType: ActorPrincipalType,
    val principal: PrincipalData,
) : PrincipalData()

data class Role(
    val roleId: RoleId,
    val premisesId: PremisesId,
    val name: String,
    val roleType: RoleType,
)

data class InternalData(val id: String) : PrincipalData()
