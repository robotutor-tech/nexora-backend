package com.robotutor.nexora.shared.domain.model


sealed class PrincipalContext()
sealed class ActorPrincipalContext() : PrincipalContext()

data class UserContext(val userId: UserId) : ActorPrincipalContext()
data class DeviceContext(val deviceId: DeviceId) : ActorPrincipalContext()

data class InternalContext(val value: String) : PrincipalContext()
data class InvitationContext(val value: InvitationId) : PrincipalContext()
data class ActorContext(val actorId: ActorId, val roleId: RoleId, val principalContext: ActorPrincipalContext) : PrincipalContext()