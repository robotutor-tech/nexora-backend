package com.robotutor.nexora.context.iam.interfaces.controller.mapper

import com.robotutor.nexora.context.iam.application.command.ValidateTokenCommand
import com.robotutor.nexora.context.iam.application.view.SessionTokens
import com.robotutor.nexora.context.iam.application.view.SessionValidationResult
import com.robotutor.nexora.context.iam.domain.vo.AccountPrincipal
import com.robotutor.nexora.context.iam.domain.vo.ActorPrincipal
import com.robotutor.nexora.context.iam.domain.vo.SessionPrincipal
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import com.robotutor.nexora.context.iam.interfaces.controller.view.*
import java.time.Instant

object SessionMapper {
    fun toTokenResponses(sessionTokens: SessionTokens): TokenResponses {
        return TokenResponses(
            token = "Bearer ".plus(sessionTokens.accessToken.value),
            refreshToken = "Bearer ".plus(sessionTokens.refreshToken.value),
        )
    }

    fun toValidateSessionCommand(token: String): ValidateTokenCommand {
        return ValidateTokenCommand(TokenValue(token.removePrefix("Bearer ")))
    }

    fun toValidateSessionResponse(sessionValidationResult: SessionValidationResult): SessionValidateResponse {
        return SessionValidateResponse(
            isValid = sessionValidationResult.isValid,
            expiresIn = sessionValidationResult.expiresAt.epochSecond - Instant.now().epochSecond,
            principal = toSessionPrincipalResponse(sessionValidationResult.sessionPrincipal),
        )
    }

    private fun toSessionPrincipalResponse(principal: SessionPrincipal): SessionPrincipalResponse {
        return when (principal) {
            is AccountPrincipal -> AccountPrincipalResponse(
                principal.accountId.value,
                principal.type,
                principal.principalId.value
            )

            is ActorPrincipal -> ActorPrincipalResponse(
                principal.actorId.value,
                principal.premisesId.value,
                principal.accountId.value,
                principal.type,
                principal.principalId.value

            )
        }
    }
}