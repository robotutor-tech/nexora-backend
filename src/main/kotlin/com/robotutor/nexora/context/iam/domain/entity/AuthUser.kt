package com.robotutor.nexora.context.iam.domain.entity

import com.robotutor.nexora.context.iam.domain.event.IAMEvent
import com.robotutor.nexora.context.user.domain.vo.UserId
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.model.Email
import java.time.Instant

data class AuthUser(
    val userId: UserId,
    val email: Email,
    val password: HashedPassword,
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
    val version: Long = 0
) : AggregateRoot<AuthUser, UserId, IAMEvent>(userId) {
    companion object {
        fun register(userId: UserId, email: Email, password: HashedPassword): AuthUser {
            val authUser = AuthUser(userId = userId, email = email, password = password)
//            authUser.addEvent(AuthUserRegisteredEvent(authUser.userId))
            return authUser
        }
    }
}


data class Password(val value: String)
data class HashedPassword(val value: String)