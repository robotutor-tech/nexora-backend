package com.robotutor.nexora.modules.auth.interfaces.controller.dto

import com.robotutor.nexora.shared.domain.model.TokenIdentifier
import java.time.Instant

data class TokenResponseDto(val token: String)
data class TokenResponsesDto(val token: String, val refreshToken: String)
data class TokenValidationResultDto(
    val isValid: Boolean,
    val principalId: String,
    val principalType: TokenIdentifier,
    val expiresAt: Instant,
)