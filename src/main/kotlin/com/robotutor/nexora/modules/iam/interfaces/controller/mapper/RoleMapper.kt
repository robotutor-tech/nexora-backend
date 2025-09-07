package com.robotutor.nexora.modules.iam.interfaces.controller.mapper

import com.robotutor.nexora.modules.iam.domain.entity.Role
import com.robotutor.nexora.modules.iam.interfaces.controller.dto.RoleResponse

object RoleMapper {
        fun toRoleResponse( role: Role): RoleResponse {
            return RoleResponse(
                roleId = role.roleId.value,
                premisesId = role.premisesId.value,
                name = role.name.value,
                roleType = role.roleType
            )
        }

        fun toRoleResponse( role: com.robotutor.nexora.shared.domain.model.Role): RoleResponse {
            return RoleResponse(
                roleId = role.roleId.value,
                premisesId = role.premisesId.value,
                name = role.name.value,
                roleType = role.roleType
            )
        }
    }
