package com.robotutor.nexora.modules.orchestration.gateway.view

import com.robotutor.nexora.common.security.models.UserId
import java.time.Instant

data class UserView(
    val userId: UserId,
    val name: String,
    val email: String,
    val registeredAt: Instant
)
