package com.robotutor.nexora.modules.auth.application.dto

import com.robotutor.nexora.modules.auth.domain.model.Token
import com.robotutor.nexora.modules.auth.domain.model.Tokens
import com.robotutor.nexora.shared.domain.model.TokenIdentifier
import java.time.Instant

data class TokenResponse(
    val value: String,
) {
    companion object {
        fun from(token: Token): TokenResponse {
            return TokenResponse(
                value = token.value.value
            )
        }
    }
}

data class TokenResponses(
    val token: TokenResponse,
    val refreshToken: TokenResponse,
) {
    companion object {
        fun from(tokens: Tokens): TokenResponses {
            return TokenResponses(
                token = TokenResponse.from(tokens.token),
                refreshToken = TokenResponse.from(tokens.refreshToken)
            )
        }
    }
}

data class TokenValidationResult(
    val isValid: Boolean,
    val principalId: String,
    val principalType: TokenIdentifier,
    val expiresAt: Instant,
)