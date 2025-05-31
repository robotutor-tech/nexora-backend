package com.robotutor.nexora.auth.controllers.views

import com.robotutor.nexora.auth.models.Token
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.security.models.Identifier
import com.robotutor.nexora.security.models.TokenIdentifier
import jakarta.validation.constraints.NotBlank

data class PremisesActorRequest(
    @field:NotBlank(message = "Actor id is required")
    val actorId: String,
    @field:NotBlank(message = "Role id is required")
    val roleId: String,
)

data class TokenView(val token: String) {
    companion object {
        fun from(token: Token): TokenView {
            return TokenView("Bearer ${token.value}")
        }
    }
}

data class AuthValidationView(val identifier: Identifier<TokenIdentifier>, val roleId: RoleId?) {
    companion object {
        fun from(token: Token): AuthValidationView {
            return AuthValidationView(identifier = token.identifier, roleId = token.role)
        }
    }
}