package com.robotutor.nexora.iam.controllers.view

import com.robotutor.nexora.iam.models.Role
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.iam.models.RoleType
import com.robotutor.nexora.premises.models.PremisesId

data class RoleView(
    val roleId: RoleId,
    val premisesId: PremisesId,
    val name: String,
    val role: RoleType,
) {
    companion object {
        fun from(role: Role): RoleView {
            return RoleView(
                roleId = role.roleId,
                premisesId = role.premisesId,
                name = role.name,
                role = role.role,
            )
        }
    }
}