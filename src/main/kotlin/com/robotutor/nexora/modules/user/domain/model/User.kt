package com.robotutor.nexora.modules.user.domain.model

import com.robotutor.nexora.shared.domain.event.DomainAggregate
import com.robotutor.nexora.shared.domain.event.UserRegisteredEvent
import com.robotutor.nexora.shared.domain.model.Email
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.UserId
import java.time.Instant

data class User(
    val userId: UserId,
    val name: Name,
    val email: Email,
    val registeredAt: Instant = Instant.now(),
    val version: Long? = null
) : DomainAggregate() {
    companion object {
        fun register(userId: UserId, name: Name, email: Email): User {
            val user = User(userId = userId, name = name, email = email)
            user.addDomainEvent(UserRegisteredEvent(user.userId))
            return user
        }
    }
}
