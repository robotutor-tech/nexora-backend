package com.robotutor.nexora.iam.controllers.view

import com.robotutor.nexora.iam.models.*
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.ActorIdentifier
import com.robotutor.nexora.security.models.Identifier
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterActorsRequest(
    @field:NotBlank(message = "Premises Id is required")
    val premisesId: PremisesId,
    @field:NotBlank(message = "Roles is required")
    @field:Size(min = 1, max = 5, message = "Roles are required")
    val roles: List<RoleId>,
)

data class RegisterActorRequest(
    @field:NotBlank(message = "Roles is required")
    val role: RoleId,
    @field:NotBlank(message = "Identifier is required")
    val identifier: String,
    @field:NotBlank(message = "Actor type is required")
    val type: ActorIdentifier,
)

data class ActorView(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val actorIdentifier: Identifier<ActorIdentifier>,
    val role: RoleView,
    val state: ActorState,
) {
    companion object {
        fun from(actor: Actor, role: Role): ActorView {
            return ActorView(
                actorId = actor.actorId,
                premisesId = actor.premisesId,
                actorIdentifier = actor.actorIdentifier,
                role = RoleView.from(role),
                state = actor.state,
            )
        }
    }
}

