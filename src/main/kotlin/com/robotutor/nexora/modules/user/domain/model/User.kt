package com.robotutor.nexora.modules.user.domain.model

import com.robotutor.nexora.shared.domain.model.UserId
import java.time.Instant

data class User(
    val userId: UserId,
    val name: String,
    val email: Email,
    val registeredAt: Instant = Instant.now(),
    val version: Long? = null
) {
    companion object {
        fun from(userId: UserId, userDetails: UserDetails): User {
            return User(
                userId = userId,
                name = userDetails.name,
                email = userDetails.email,
                registeredAt = userDetails.registeredAt,
                version = userDetails.version
            )
        }
    }
}
