package com.robotutor.nexora.modules.iam.controllers.view

import com.robotutor.nexora.modules.iam.models.Actor
import com.robotutor.nexora.modules.iam.models.ActorState
import com.robotutor.nexora.modules.iam.models.Role
import com.robotutor.nexora.modules.iam.models.RoleId
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.common.security.models.ActorId
import com.robotutor.nexora.shared.domain.model.ActorIdentifier
import com.robotutor.nexora.shared.domain.model.Identifier
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterActorRequest(
    @field:NotBlank(message = "Roles is required")
    @field:Size(min = 1, message = "Roles are required")
    val roles: List<RoleId>,
    @field:NotBlank(message = "Identifier is required")
    val identifier: Identifier<ActorIdentifier>,
    @field:NotBlank(message = "PremisesId is required")
    val premisesId: PremisesId,
)

data class ActorView(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val identifier: Identifier<ActorIdentifier>,
    val role: Role,
    val state: ActorState,
) {
    companion object {
        fun from(actor: Actor, role: Role): ActorView {
            return ActorView(
                actorId = actor.actorId,
                premisesId = actor.premisesId,
                identifier = actor.identifier,
                role = role,
                state = actor.state,
            )
        }
    }
}

data class ActorWithRolesView(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val identifier: Identifier<ActorIdentifier>,
    val roles: List<RoleIdWithName>,
    val state: ActorState,
) {
    companion object {
        fun from(actor: Actor, roles: List<Role>): ActorWithRolesView {
            return ActorWithRolesView(
                actorId = actor.actorId,
                premisesId = actor.premisesId,
                identifier = actor.identifier,
                roles = roles.map { RoleIdWithName.from(it) },
                state = actor.state,
            )
        }
    }
}

data class RoleIdWithName(val roleId: RoleId, val name: String) {
    companion object {
        fun from(role: Role): RoleIdWithName {
            return RoleIdWithName(role.roleId, role.name)
        }
    }

}
