package com.robotutor.nexora.modules.auth.domain.model

import com.robotutor.nexora.shared.domain.model.UserId
import java.time.Instant

data class AuthUser(
    val userId: UserId,
    val email: Email,
    val password: HashedPassword,
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
    val version: Long? = null
)

data class Email(val value: String)
data class Password(val value: String)
data class HashedPassword(val value: String)