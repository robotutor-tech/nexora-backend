package com.robotutor.nexora.modules.iam.interfaces.controller.mapper

import com.robotutor.nexora.modules.iam.domain.model.Role
import com.robotutor.nexora.modules.iam.interfaces.controller.dto.RoleResponse

class RoleMapper {
    companion object {
        fun toRoleResponse( role: Role): RoleResponse {
            return RoleResponse(
                roleId = role.roleId.value,
                premisesId = role.premisesId.value,
                name = role.name,
                roleType = role.roleType
            )
        }
    }
}