package com.robotutor.nexora.common.security.domain.model

import com.robotutor.nexora.modules.auth.domain.entity.TokenPrincipalType
import com.robotutor.nexora.shared.domain.model.PrincipalContext
import java.time.Instant

data class ValidateTokenResult(
    val isValid: Boolean,
    val principalType: TokenPrincipalType,
    val principal: PrincipalContext,
    val expiresAt: Instant,
)