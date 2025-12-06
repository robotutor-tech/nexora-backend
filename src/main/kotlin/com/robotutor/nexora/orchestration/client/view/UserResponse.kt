package com.robotutor.nexora.orchestration.client.view

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
