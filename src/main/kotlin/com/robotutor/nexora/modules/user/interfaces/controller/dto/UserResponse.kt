package com.robotutor.nexora.modules.user.interfaces.controller.dto

import java.time.Instant

data class UserResponse(
    val userId: String,
    val name: String,
    val email: String,
    val registeredAt: Instant
)