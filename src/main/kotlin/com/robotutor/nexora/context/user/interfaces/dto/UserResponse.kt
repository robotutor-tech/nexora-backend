package com.robotutor.nexora.context.user.interfaces.dto

import java.time.Instant

data class UserResponse(
        val userId: String,
        val name: String,
        val email: String,
        val mobile: String,
        val isEmailVerified: Boolean,
        val isMobileVerified: Boolean,
        val registeredAt: Instant
)
