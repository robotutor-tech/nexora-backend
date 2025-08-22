package com.robotutor.nexora.shared.domain.model


sealed class PrincipalContext()

data class UserContext(val userId: UserId) : PrincipalContext()

data class DeviceContext(val deviceId: DeviceId) : PrincipalContext()

data class InternalContext(val value: String) : PrincipalContext()

data class ServerContext(val serverId: String) : PrincipalContext()

data class ActorContext(val actorId: ActorId, val roleId: RoleId) : PrincipalContext()