package com.robotutor.nexora.modules.iam.interfaces.controller.mapper

import com.robotutor.nexora.modules.iam.domain.entity.Actor
import com.robotutor.nexora.modules.iam.domain.entity.Role
import com.robotutor.nexora.modules.iam.interfaces.controller.dto.ActorResponse
import com.robotutor.nexora.modules.iam.interfaces.controller.dto.ActorWithRolesResponse
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.interfaces.mapper.PrincipalContextMapper

class ActorMapper {
    companion object {
        fun toActorWithRolesResponse(actor: Actor, roles: List<Role>): ActorWithRolesResponse {
            return ActorWithRolesResponse(
                actorId = actor.actorId.value,
                premisesId = actor.premisesId.value,
                roles = roles.map { RoleMapper.toRoleResponse(it) },
                principalType = actor.principalType,
                principal = PrincipalContextMapper.toActorPrincipalContextResponse(actor.principal),
                state = actor.state,
            )
        }

        fun toActorResponse(actor: Actor, role: Role): ActorResponse {
            return ActorResponse(
                actorId = actor.actorId.value,
                premisesId = actor.premisesId.value,
                role = RoleMapper.toRoleResponse(role),
                principalType = actor.principalType,
                principal = PrincipalContextMapper.toActorPrincipalContextResponse(actor.principal),
            )
        }

        fun toActorResponse(actorData: ActorData): ActorResponse {
            return ActorResponse(
                actorId = actorData.actorId.value,
                premisesId = actorData.premisesId.value,
                role = RoleMapper.toRoleResponse(actorData.role),
                principalType = actorData.principalType,
                principal = PrincipalContextMapper.toActorPrincipalContextResponse(actorData.principal),
            )
        }
    }
}