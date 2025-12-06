package com.robotutor.nexora.context.iam.interfaces.controller.mapper

import com.robotutor.nexora.context.iam.application.command.ValidateTokenCommand
import com.robotutor.nexora.context.iam.application.view.TokenValidationResult
import com.robotutor.nexora.context.iam.application.view.Tokens
import com.robotutor.nexora.context.iam.domain.vo.AccountTokenPrincipalContext
import com.robotutor.nexora.context.iam.domain.vo.ActorTokenPrincipalContext
import com.robotutor.nexora.context.iam.domain.vo.TokenPrincipalContext
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import com.robotutor.nexora.context.iam.interfaces.controller.view.AccountTokenPrincipalContextResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.ActorTokenPrincipalContextResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.TokenPrincipalContextResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.TokenResponses
import com.robotutor.nexora.context.iam.interfaces.controller.view.TokenValidationResponse
import java.time.Instant

object TokenMapper {
    fun toTokenResponses(tokens: Tokens): TokenResponses {
        return TokenResponses(
            token = "Bearer ".plus(tokens.authorizationToken.value.value),
            refreshToken = "Bearer ".plus(tokens.refreshToken.value.value),
        )
    }

    fun toValidateTokenCommand(token: String): ValidateTokenCommand {
        return ValidateTokenCommand(TokenValue(token.removePrefix("Bearer ")))
    }

    fun toValidateTokenResponse(tokenValidationResult: TokenValidationResult): TokenValidationResponse {
        return TokenValidationResponse(
            isValid = tokenValidationResult.isValid,
            principalType = tokenValidationResult.principalType,
            expiresIn = tokenValidationResult.expiresAt.epochSecond - Instant.now().epochSecond,
            principal = toPrincipalContext(tokenValidationResult.principal),
        )
    }

    private fun toPrincipalContext(principal: TokenPrincipalContext): TokenPrincipalContextResponse {
        return when (principal) {
            is AccountTokenPrincipalContext -> AccountTokenPrincipalContextResponse(
                accountId = principal.accountId.value,
                type = principal.type
            )

            is ActorTokenPrincipalContext -> ActorTokenPrincipalContextResponse(principal.actorId, principal.roleId)
        }
    }
//
//    fun toRefreshTokenCommand(token: String): RefreshTokenCommand {
//        return RefreshTokenCommand(token.removePrefix("Bearer "))
//    }
}