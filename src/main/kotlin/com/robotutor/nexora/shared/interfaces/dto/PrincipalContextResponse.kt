package com.robotutor.nexora.shared.interfaces.dto

sealed interface PrincipalContextResponse

data class UserContextResponse(val userId: String) : PrincipalContextResponse

data class DeviceContextResponse(val deviceId: String) : PrincipalContextResponse

data class InternalContextResponse(val value: String) : PrincipalContextResponse

data class ServerContextResponse(val serverId: String) : PrincipalContextResponse

data class ActorContextResponse(val actorId: String, val roleId: String) : PrincipalContextResponse
