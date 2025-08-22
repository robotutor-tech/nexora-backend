package com.robotutor.nexora.modules.iam.interfaces.controller.mapper

import com.robotutor.nexora.modules.iam.domain.model.Actor
import com.robotutor.nexora.modules.iam.domain.model.Role
import com.robotutor.nexora.modules.iam.interfaces.controller.dto.ActorResponse
import com.robotutor.nexora.modules.iam.interfaces.controller.dto.ActorWithRolesResponse
import com.robotutor.nexora.shared.interfaces.mapper.PrincipalContextResponseMapper

class ActorMapper {
    companion object {
        fun toActorWithRolesResponse(actor: Actor, roles: List<Role>): ActorWithRolesResponse {
            return ActorWithRolesResponse(
                actorId = actor.actorId.value,
                premisesId = actor.premisesId.value,
                roles = roles.map { RoleMapper.toRoleResponse(it) },
                principalType = actor.principalType,
                principal = PrincipalContextResponseMapper.toPrincipalContextResponse(actor.principal),
                state = actor.state,
            )
        }

        fun toActorResponse(actor: Actor, role: Role): ActorResponse {
            return ActorResponse(
                actorId = actor.actorId.value,
                premisesId = actor.premisesId.value,
                role = RoleMapper.toRoleResponse(role),
                state = actor.state,
                principalType = actor.principalType,
                principal = PrincipalContextResponseMapper.toPrincipalContextResponse(actor.principal),
            )
        }
    }
}