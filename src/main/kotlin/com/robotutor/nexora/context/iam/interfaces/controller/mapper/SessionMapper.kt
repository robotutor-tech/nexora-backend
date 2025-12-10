package com.robotutor.nexora.context.iam.interfaces.controller.mapper

import com.robotutor.nexora.context.iam.application.command.ValidateTokenCommand
import com.robotutor.nexora.context.iam.application.view.SessionValidationResult
import com.robotutor.nexora.context.iam.application.view.SessionTokens
import com.robotutor.nexora.context.iam.domain.vo.AccountPrincipal
import com.robotutor.nexora.context.iam.domain.vo.ActorPrincipal
import com.robotutor.nexora.context.iam.domain.vo.SessionPrincipal
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import com.robotutor.nexora.context.iam.interfaces.controller.view.AccountPrincipalResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.ActorPrincipalResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.SessionPrincipalResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.TokenResponses
import com.robotutor.nexora.context.iam.interfaces.controller.view.SessionValidateResponse
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
            principal = toPrincipalContext(sessionValidationResult.sessionPrincipal),
        )
    }

    private fun toPrincipalContext(principal: SessionPrincipal): SessionPrincipalResponse {
        return when (principal) {
            is AccountPrincipal -> AccountPrincipalResponse(principal.accountId.value, principal.accountType)
            is ActorPrincipal -> ActorPrincipalResponse(
                principal.actorId.value,
                principal.premisesId.value,
                principal.accountPrincipal.accountId.value,
                principal.accountPrincipal.accountType
            )
        }
    }
//
//    fun toRefreshTokenCommand(token: String): RefreshTokenCommand {
//        return RefreshTokenCommand(token.removePrefix("Bearer "))
//    }
}