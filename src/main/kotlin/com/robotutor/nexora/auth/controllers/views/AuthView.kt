package com.robotutor.nexora.auth.controllers.views

import com.robotutor.nexora.iam.controllers.view.RoleView
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.iam.models.RoleType
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class AuthUserRequest(
    @field:NotBlank(message = "UserId is required")
    val userId: UserId,
    @field:Email(message = "Email should be valid")
    val email: String,
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    @field:Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+\$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit"
    )
    val password: String
)

data class AuthLoginRequest(
    @field:Email(message = "Email should be valid")
    val email: String,
    @field:NotBlank(message = "Password is required")
    val password: String
)

data class PremisesActorDataView(
    val actorId: ActorId,
    val role: RoleWithPolicyTypeView,
    val premisesId: PremisesId,
    val identifier: Identifier<ActorIdentifier>
) : IAuthenticationData {
    companion object {
        fun from(premisesActorData: PremisesActorData): PremisesActorDataView {
            return PremisesActorDataView(
                actorId = premisesActorData.actorId,
                role = RoleWithPolicyTypeView.from(premisesActorData.role),
                premisesId = premisesActorData.premisesId,
                identifier = premisesActorData.identifier
            )
        }
    }
}

data class RoleWithPolicyTypeView(
    val roleId: RoleId,
    val premisesId: PremisesId,
    val name: String,
    val role: RoleType,
) {
    companion object {
        fun from(role: RoleView): RoleWithPolicyTypeView {
            return RoleWithPolicyTypeView(
                roleId = role.roleId,
                premisesId = role.premisesId,
                name = role.name,
                role = role.role,
            )
        }
    }
}