package com.robotutor.nexora.context.iam.interfaces.controller.mapper

import com.robotutor.nexora.context.iam.application.command.RefreshTokenCommand
import com.robotutor.nexora.context.iam.application.command.ValidateTokenCommand
import com.robotutor.nexora.context.iam.application.dto.TokenResponses
import com.robotutor.nexora.context.iam.application.dto.TokenValidationResult
import com.robotutor.nexora.context.iam.interfaces.controller.view.TokenResponsesDto
import com.robotutor.nexora.context.iam.interfaces.controller.view.TokenValidationResultDto
import java.time.Instant

object TokenMapper {
    fun toTokenResponsesDto(tokenResponses: TokenResponses): TokenResponsesDto {
        return TokenResponsesDto("Bearer " + tokenResponses.token.value, "Bearer " + tokenResponses.refreshToken.value)
    }

    fun toValidateTokenCommand(token: String): ValidateTokenCommand {
        return ValidateTokenCommand(token.removePrefix("Bearer "))
    }

    fun toValidateTokenResultDto(tokenValidationResult: TokenValidationResult): TokenValidationResultDto {
        return TokenValidationResultDto(
            isValid = tokenValidationResult.isValid,
            principalType = tokenValidationResult.principalType,
            expiresIn = tokenValidationResult.expiresAt.epochSecond - Instant.now().epochSecond,
            principal = tokenValidationResult.principal,
        )
    }

    fun toRefreshTokenCommand(token: String): RefreshTokenCommand {
        return RefreshTokenCommand(token.removePrefix("Bearer "))
    }
}