package com.robotutor.nexora.modules.auth.application.dto

import com.robotutor.nexora.modules.auth.domain.entity.Token
import com.robotutor.nexora.modules.auth.domain.entity.TokenPrincipalType
import com.robotutor.nexora.modules.auth.domain.entity.Tokens
import com.robotutor.nexora.shared.interfaces.dto.PrincipalContextResponse
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
    val principal: PrincipalContextResponse,
    val principalType: TokenPrincipalType,
    val expiresAt: Instant,
)