package com.robotutor.nexora.modules.user.domain.model

import java.time.Instant

data class UserDetails(
    val name: String,
    val email: Email,
    val registeredAt: Instant = Instant.now(),
    val version: Long? = null
)

@JvmInline
value class Email(val value: String)
