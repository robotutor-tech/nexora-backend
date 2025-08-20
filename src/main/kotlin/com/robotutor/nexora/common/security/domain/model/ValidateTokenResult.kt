package com.robotutor.nexora.common.security.domain.model

import com.robotutor.nexora.shared.domain.model.TokenIdentifier
import java.time.Instant

data class ValidateTokenResult(
    val isValid: Boolean,
    val principalId: String,
    val principalType: TokenIdentifier,
    val expiresAt: Instant,
)