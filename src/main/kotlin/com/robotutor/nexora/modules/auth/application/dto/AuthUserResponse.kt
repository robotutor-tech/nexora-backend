package com.robotutor.nexora.modules.auth.application.dto

import com.robotutor.nexora.modules.auth.domain.model.AuthUser
import com.robotutor.nexora.modules.auth.domain.model.Email
import com.robotutor.nexora.shared.domain.model.UserId
import java.time.Instant

data class AuthUserResponse(
    val userId: UserId,
    val email: Email,
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
) {
    companion object {
        fun from(authUser : AuthUser): AuthUserResponse {
            return AuthUserResponse(
                userId = authUser.userId,
                email = authUser.email,
                createdAt = authUser.createdAt,
                updatedAt = authUser.updatedAt
            )
        }
    }
}