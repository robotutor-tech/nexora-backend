package com.robotutor.nexora.modules.auth.gateways.view

import com.robotutor.nexora.modules.iam.models.ActorState
import com.robotutor.nexora.modules.iam.models.Role
import com.robotutor.nexora.modules.iam.models.RoleId
import com.robotutor.nexora.modules.iam.models.RoleType
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.common.security.models.ActorId
import com.robotutor.nexora.common.security.models.ActorIdentifier
import com.robotutor.nexora.common.security.models.Identifier

data class ActorView(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val identifier: Identifier<ActorIdentifier>,
    val role: RoleView,
    val state: ActorState,
)

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

