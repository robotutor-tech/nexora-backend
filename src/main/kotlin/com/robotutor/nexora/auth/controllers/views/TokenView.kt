package com.robotutor.nexora.auth.controllers.views

import com.robotutor.nexora.auth.models.ActorData
import com.robotutor.nexora.auth.models.Token
import com.robotutor.nexora.auth.models.TokenIdentifierType
import com.robotutor.nexora.premises.models.PremisesId
import jakarta.validation.constraints.NotBlank

data class PremisesActorRequest(
    @field:NotBlank(message = "PremisesId is required")
    val premisesId: PremisesId,
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

data class AuthValidationView(
    val identifier: String,
    val identifierType: TokenIdentifierType,
    val premisesId: PremisesId?,
    val actor: ActorData?
) {
    companion object {
        fun from(token: Token): AuthValidationView {
            return AuthValidationView(
                identifier = token.metadata.identifier,
                identifierType = token.metadata.identifierType,
                premisesId = token.metadata.premisesId,
                actor = token.metadata.actor
            )
        }
    }
}