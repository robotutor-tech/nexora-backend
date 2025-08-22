package com.robotutor.nexora.shared.interfaces.mapper

import com.robotutor.nexora.shared.domain.model.ActorContext
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.DeviceContext
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.InternalContext
import com.robotutor.nexora.shared.domain.model.PrincipalContext
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.domain.model.ServerContext
import com.robotutor.nexora.shared.domain.model.UserContext
import com.robotutor.nexora.shared.domain.model.UserId
import com.robotutor.nexora.shared.interfaces.dto.ActorContextResponse
import com.robotutor.nexora.shared.interfaces.dto.DeviceContextResponse
import com.robotutor.nexora.shared.interfaces.dto.InternalContextResponse
import com.robotutor.nexora.shared.interfaces.dto.PrincipalContextResponse
import com.robotutor.nexora.shared.interfaces.dto.ServerContextResponse
import com.robotutor.nexora.shared.interfaces.dto.UserContextResponse

class PrincipalContextResponseMapper {
    companion object {
        fun toPrincipalContextResponse(context: PrincipalContext): PrincipalContextResponse {
            return when (context) {
                is ActorContext -> ActorContextResponse(context.actorId.value, context.roleId.value)
                is DeviceContext -> DeviceContextResponse(context.deviceId.value)
                is InternalContext -> InternalContextResponse("Internal Context")
                is ServerContext -> ServerContextResponse("Server Context")
                is UserContext -> UserContextResponse(context.userId.value)
            }
        }

        fun toPrincipalContext(principal: PrincipalContextResponse): PrincipalContext {
            return when (principal) {
                is ActorContextResponse -> ActorContext(ActorId(principal.actorId), RoleId(principal.roleId))
                is DeviceContextResponse -> DeviceContext(DeviceId(principal.deviceId))
                is InternalContextResponse -> InternalContext("Internal Context")
                is ServerContextResponse -> ServerContext("server context")
                is UserContextResponse -> UserContext(UserId(principal.userId))
            }
        }
    }
}