package com.robotutor.nexora.auth.controllers.views

import com.robotutor.nexora.auth.models.Token
import com.robotutor.nexora.security.models.Identifier
import com.robotutor.nexora.security.models.TokenIdentifier
import jakarta.validation.constraints.NotBlank

data class PremisesActorRequest(
    @field:NotBlank(message = "Actor id is required")
    val actorId: String,
)

data class TokenView(val token: String) {
    companion object {
        fun from(token: Token): TokenView {
            return TokenView("Bearer ${token.value}")
        }
    }
}

data class AuthValidationView(val tokenIdentifier: Identifier<TokenIdentifier>) {
    companion object {
        fun from(token: Token): AuthValidationView {
            return AuthValidationView(tokenIdentifier = token.tokenIdentifier)
        }
    }
}