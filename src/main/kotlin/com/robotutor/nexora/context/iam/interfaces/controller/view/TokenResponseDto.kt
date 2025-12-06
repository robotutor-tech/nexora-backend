package com.robotutor.nexora.context.iam.interfaces.controller.view

import com.robotutor.nexora.context.iam.domain.entity.TokenPrincipalType
import com.robotutor.nexora.shared.interfaces.dto.PrincipalContextResponse


data class TokenResponsesDto(val token: String, val refreshToken: String)
data class TokenValidationResultDto(
    val isValid: Boolean,
    val principalType: TokenPrincipalType,
    val principal: PrincipalContextResponse,
    val expiresIn: Number,
)