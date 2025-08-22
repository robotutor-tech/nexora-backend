package com.robotutor.nexora.modules.premises.adapters.client.mapper

import com.robotutor.nexora.modules.iam.interfaces.controller.dto.ActorWithRolesResponse
import com.robotutor.nexora.modules.iam.interfaces.controller.dto.RoleResponse
import com.robotutor.nexora.modules.premises.application.facade.dto.ActorWithRoles
import com.robotutor.nexora.modules.premises.application.facade.dto.Role
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.interfaces.mapper.PrincipalContextResponseMapper

class IAMMapper {
    companion object {
        fun toActorWithRoles(actorWithRolesResponse: ActorWithRolesResponse): ActorWithRoles {
            return ActorWithRoles(
                actorId = ActorId(actorWithRolesResponse.actorId),
                premisesId = PremisesId(actorWithRolesResponse.premisesId),
                roles = actorWithRolesResponse.roles.map { toRole(it) },
                principalType = actorWithRolesResponse.principalType,
                principal = PrincipalContextResponseMapper.toPrincipalContext(actorWithRolesResponse.principal),
            )
        }

        fun toRole(roleResponse: RoleResponse): Role {
            return Role(
                roleId = RoleId(roleResponse.roleId),
                premisesId = PremisesId(roleResponse.premisesId),
                name = roleResponse.name,
                roleType = roleResponse.roleType
            )
        }
    }
}