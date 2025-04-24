package com.robotutor.nexora.orchestration.gateway.view

import com.robotutor.nexora.security.models.UserId
import java.time.LocalDateTime

data class UserView(
    val userId: UserId,
    val name: String,
    val email: String,
    val registeredAt: LocalDateTime
)
