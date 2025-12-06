package com.robotutor.nexora.shared.domain.model

import com.robotutor.nexora.context.user.domain.vo.UserId

sealed interface PrincipalContext
sealed interface ActorPrincipalContext : PrincipalContext

data class UserContext(val userId: UserId) : ActorPrincipalContext
data class DeviceContext(val deviceId: DeviceId) : ActorPrincipalContext

data class InternalContext(val value: String) : PrincipalContext
data class InvitationContext(val invitationId: InvitationId) : PrincipalContext
data class ActorContext(val actorId: ActorId, val roleId: RoleId, val principalContext: ActorPrincipalContext) :
    PrincipalContext