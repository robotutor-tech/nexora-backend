package com.robotutor.nexora.modules.user.adapters.inbound.conroller.dto

import com.robotutor.nexora.common.security.models.UserId
import java.time.Instant

data class UserResponse(
    val userId: UserId,
    val name: String,
    val email: String,
    val registeredAt: Instant
)