package com.robotutor.nexora.common.security.domain.model

import com.robotutor.nexora.shared.domain.model.PrincipalContext
import com.robotutor.nexora.shared.domain.model.TokenPrincipalType
import java.time.Instant

data class ValidateTokenResult(
    val isValid: Boolean,
    val principalType: TokenPrincipalType,
    val principal: PrincipalContext,
    val expiresAt: Instant,
)