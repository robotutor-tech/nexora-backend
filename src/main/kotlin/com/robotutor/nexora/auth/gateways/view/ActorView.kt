package com.robotutor.nexora.auth.gateways.view

import com.robotutor.nexora.iam.models.ActorState
import com.robotutor.nexora.iam.models.Role
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.iam.models.RoleType
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.ActorIdentifier
import com.robotutor.nexora.security.models.Identifier

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

