package com.robotutor.nexora.shared.interfaces.dto

sealed interface PrincipalContextResponse
sealed interface ActorPrincipalContextResponse : PrincipalContextResponse

data class UserContextResponse(val userId: String) : ActorPrincipalContextResponse
data class DeviceContextResponse(val deviceId: String) : ActorPrincipalContextResponse
data class InvitationContextResponse(val invitationId: String) : PrincipalContextResponse
data class InternalContextResponse(val value: String) : PrincipalContextResponse
data class ActorContextResponse(
    val actorId: String,
    val roleId: String,
    val context: ActorPrincipalContextResponse
) : PrincipalContextResponse
