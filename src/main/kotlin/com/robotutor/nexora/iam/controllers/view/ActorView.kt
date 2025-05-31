package com.robotutor.nexora.iam.controllers.view

import com.robotutor.nexora.iam.models.*
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.ActorIdentifier
import com.robotutor.nexora.security.models.Identifier
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

data class ActorWithRoleView(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val identifier: Identifier<ActorIdentifier>,
    val role: RoleView,
    val state: ActorState,
) {
    companion object {
        fun from(actor: Actor, role: Role, policies: List<Policy>): ActorWithRoleView {
            return ActorWithRoleView(
                actorId = actor.actorId,
                premisesId = actor.premisesId,
                identifier = actor.identifier,
                role = RoleView.from(role, policies),
                state = actor.state,
            )
        }
    }
}

data class ActorView(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val identifier: Identifier<ActorIdentifier>,
    val roles: List<RoleIdWithName>,
    val state: ActorState,
) {
    companion object {
        fun from(actor: Actor, roles: List<Role>): ActorView {
            return ActorView(
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
