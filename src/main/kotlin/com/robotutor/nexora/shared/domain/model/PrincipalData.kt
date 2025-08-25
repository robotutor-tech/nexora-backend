package com.robotutor.nexora.shared.domain.model

import java.time.Instant

sealed class PrincipalData
sealed class ActorPrincipalData : PrincipalData()

data class UserData(
    val userId: UserId,
    val name: Name,
    val email: Email,
    val registeredAt: Instant
) : ActorPrincipalData()

data class DeviceData(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    val name: Name,
) : ActorPrincipalData()

data class ActorData(
    val actorId: ActorId,
    val role: Role,
    val premisesId: PremisesId,
    val principalType: ActorPrincipalType,
    val principal: ActorPrincipalData,
) : PrincipalData()

data class Role(
    val roleId: RoleId,
    val premisesId: PremisesId,
    val name: Name,
    val roleType: RoleType,
)

data class InternalData(val id: String) : PrincipalData()

data class InvitationData(
    val invitationId: InvitationId,
    val premisesId: PremisesId,
    val name: Name,
    val zoneId: ZoneId,
    val invitedBy: ActorId,
) : PrincipalData()
