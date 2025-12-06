package com.robotutor.nexora.context.iam.application.view

import com.robotutor.nexora.context.iam.domain.aggregate.TokenPrincipalType
import com.robotutor.nexora.context.iam.domain.vo.TokenPrincipalContext
import java.time.Instant

data class TokenValidationResult(
    val isValid: Boolean,
    val principal: TokenPrincipalContext,
    val principalType: TokenPrincipalType,
    val expiresAt: Instant,
)
