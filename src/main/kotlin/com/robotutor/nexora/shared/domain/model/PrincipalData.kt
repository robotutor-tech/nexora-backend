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
    val roleId: RoleId,
    val user: UserData
) : PrincipalData()


data class InternalData(val id: String) : PrincipalData()
