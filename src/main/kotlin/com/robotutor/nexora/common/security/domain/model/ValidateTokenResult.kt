package com.robotutor.nexora.common.security.domain.model

import com.robotutor.nexora.context.iam.domain.entity.TokenPrincipalType
import com.robotutor.nexora.shared.domain.model.PrincipalContext

data class ValidateTokenResult(
    val isValid: Boolean,
    val principalType: TokenPrincipalType,
    val principal: PrincipalContext,
    val expiresIn: Number,
)