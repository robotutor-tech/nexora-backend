package com.robotutor.nexora.common.security.domain.vo

data class TokenValidationResult(
    val isValid: Boolean,
    val principal: PrincipalContext,
    val expiresIn: Number,
)

