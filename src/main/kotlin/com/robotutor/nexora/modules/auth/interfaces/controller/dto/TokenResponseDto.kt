package com.robotutor.nexora.modules.auth.interfaces.controller.dto

import com.robotutor.nexora.shared.domain.model.TokenPrincipalType
import com.robotutor.nexora.shared.interfaces.dto.PrincipalContextResponse
import java.time.Instant

data class TokenResponsesDto(val token: String, val refreshToken: String)
data class TokenValidationResultDto(
    val isValid: Boolean,
    val principalType: TokenPrincipalType,
    val principal: PrincipalContextResponse,
    val expiresAt: Instant,
)