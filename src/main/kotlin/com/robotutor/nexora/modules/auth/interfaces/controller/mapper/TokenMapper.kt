package com.robotutor.nexora.modules.auth.interfaces.controller.mapper

import com.robotutor.nexora.modules.auth.application.command.RefreshTokenCommand
import com.robotutor.nexora.modules.auth.application.command.ValidateTokenCommand
import com.robotutor.nexora.modules.auth.application.dto.TokenResponse
import com.robotutor.nexora.modules.auth.application.dto.TokenResponses
import com.robotutor.nexora.modules.auth.application.dto.TokenValidationResult
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.TokenResponseDto
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.TokenResponsesDto
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.TokenValidationResultDto

object TokenMapper {
    fun toTokenResponseDto(tokenResponse: TokenResponse): TokenResponseDto {
        return TokenResponseDto(
            token = "Bearer " + tokenResponse.value
        )
    }

    fun toTokenResponsesDto(tokenResponses: TokenResponses): TokenResponsesDto {
        return TokenResponsesDto("Bearer " + tokenResponses.token.value, "Bearer " + tokenResponses.refreshToken.value)
    }

    fun toValidateTokenCommand(token: String): ValidateTokenCommand {
        return ValidateTokenCommand(token.removePrefix("Bearer "))
    }

    fun toValidateTokenResultDto(tokenValidationResult: TokenValidationResult): TokenValidationResultDto {
        return TokenValidationResultDto(
            isValid = tokenValidationResult.isValid,
            principalId = tokenValidationResult.principalId,
            principalType = tokenValidationResult.principalType,
            expiresAt = tokenValidationResult.expiresAt,
        )
    }

    fun toRefreshTokenCommand(token: String): RefreshTokenCommand {
        return RefreshTokenCommand(token.removePrefix("Bearer "))
    }
}