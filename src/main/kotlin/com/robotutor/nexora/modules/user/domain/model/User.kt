package com.robotutor.nexora.modules.user.domain.model

import com.robotutor.nexora.shared.domain.model.UserId
import java.time.Instant

data class User(
    val userId: UserId,
    val name: String,
    val email: Email,
    val registeredAt: Instant = Instant.now(),
    val version: Long? = null
)

@JvmInline
value class Email(val value: String)
