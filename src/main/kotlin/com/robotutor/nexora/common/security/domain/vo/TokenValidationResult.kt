package com.robotutor.nexora.common.security.domain.vo

import com.robotutor.nexora.context.iam.domain.aggregate.TokenPrincipalType


data class TokenValidationResult(
    val isValid: Boolean,
    val principalType: TokenPrincipalType,
    val principal: PrincipalContext,
    val expiresIn: Number,
)

