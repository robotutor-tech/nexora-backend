package com.robotutor.nexora.modules.auth.domain.model

import com.robotutor.nexora.modules.auth.domain.event.AuthUserRegisteredEvent
import com.robotutor.nexora.shared.domain.event.DomainAggregate
import com.robotutor.nexora.shared.domain.model.Email
import com.robotutor.nexora.shared.domain.model.UserId
import java.time.Instant

data class AuthUser(
    val userId: UserId,
    val email: Email,
    val password: HashedPassword,
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
    val version: Long? = null
) : DomainAggregate() {
    companion object {
        fun register(userId: UserId, email: Email, password: HashedPassword): AuthUser {
            val authUser = AuthUser(userId = userId, email = email, password = password)
            authUser.addDomainEvent(AuthUserRegisteredEvent(authUser.userId))
            return authUser
        }
    }
}


data class Password(val value: String)
data class HashedPassword(val value: String)