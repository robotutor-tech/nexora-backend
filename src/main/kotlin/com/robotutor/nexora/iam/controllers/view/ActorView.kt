package com.robotutor.nexora.iam.controllers.view

import com.robotutor.nexora.iam.models.*
import com.robotutor.nexora.premises.models.PremisesId
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterActorRequest(
    @field:NotBlank(message = "Premises Id is required")
    val premisesId: PremisesId,
    @field:NotBlank(message = "Roles is required")
    @field:Size(min = 1, max = 5, message = "Roles are required")
    val roles: List<RoleId>,
    @field:NotBlank(message = "Identifier is required")
    val identifier: String,
    @field:NotBlank(message = "Actor type is required")
    val type: ActorType,
)

data class ActorView(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val type: ActorType,
    val identifier: String,
    val role: RoleView,
    val state: ActorState,
) {
    companion object {
        fun from(actor: Actor, role: Role): ActorView {
            return ActorView(
                actorId = actor.actorId,
                premisesId = actor.premisesId,
                type = actor.type,
                identifier = actor.identifier,
                role = RoleView.from(role),
                state = actor.state,
            )
        }
    }
}

