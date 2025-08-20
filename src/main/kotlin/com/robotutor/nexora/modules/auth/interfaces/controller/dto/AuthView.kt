package com.robotutor.nexora.modules.auth.interfaces.controller.dto

import com.robotutor.nexora.common.security.models.*
import com.robotutor.nexora.modules.iam.models.Role
import com.robotutor.nexora.modules.iam.models.RoleId
import com.robotutor.nexora.modules.iam.models.RoleType
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.shared.domain.model.ActorIdentifier
import com.robotutor.nexora.shared.domain.model.Identifier
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank


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
        fun from(role: Role): RoleWithPolicyTypeView {
            return RoleWithPolicyTypeView(
                roleId = role.roleId,
                premisesId = role.premisesId,
                name = role.name,
                role = role.role,
            )
        }
    }
}