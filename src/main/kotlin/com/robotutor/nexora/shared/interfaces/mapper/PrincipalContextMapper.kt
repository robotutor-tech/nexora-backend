package com.robotutor.nexora.shared.interfaces.mapper

import com.robotutor.nexora.shared.domain.model.*
import com.robotutor.nexora.shared.interfaces.dto.*

object PrincipalContextMapper {
    fun toPrincipalContextResponse(context: PrincipalContext): PrincipalContextResponse {
        return when (context) {
            is DeviceContext -> DeviceContextResponse(context.deviceId.value)
            is InternalContext -> InternalContextResponse("Internal Context")
            is UserContext -> UserContextResponse(context.userId.value)
            is InvitationContext -> InvitationContextResponse(context.value.value)
            is ActorContext -> ActorContextResponse(
                actorId = context.actorId.value,
                roleId = context.roleId.value,
                context = toActorPrincipalContextResponse(context.principalContext)
            )
        }
    }

    fun toPrincipalContext(contextResponse: PrincipalContextResponse): PrincipalContext {
        return when (contextResponse) {
            is DeviceContextResponse -> DeviceContext(DeviceId(contextResponse.deviceId))
            is InternalContextResponse -> InternalContext("Internal Context")
            is UserContextResponse -> UserContext(UserId(contextResponse.userId))
            is InvitationContextResponse -> InvitationContext(InvitationId(contextResponse.invitationId))
            is ActorContextResponse -> ActorContext(
                actorId = ActorId(contextResponse.actorId),
                roleId = RoleId(contextResponse.roleId),
                principalContext = toActorPrincipalContext((contextResponse.context))
            )
        }
    }

    fun toActorPrincipalContext(principal: ActorPrincipalContextResponse): ActorPrincipalContext {
        return when (principal) {
            is DeviceContextResponse -> DeviceContext(DeviceId(principal.deviceId))
            is UserContextResponse -> UserContext(UserId(principal.userId))
        }
    }

    fun toActorPrincipalContextResponse(principal: ActorPrincipalContext): ActorPrincipalContextResponse {
        return when (principal) {
            is DeviceContext -> DeviceContextResponse(principal.deviceId.value)
            is UserContext -> UserContextResponse(principal.userId.value)
        }
    }

    fun toActorPrincipalContextResponse(principalData: ActorPrincipalData): ActorPrincipalContextResponse {
        return when (principalData) {
            is DeviceData -> DeviceContextResponse(principalData.deviceId.value)
            is UserData -> UserContextResponse(principalData.userId.value)
        }
    }
}