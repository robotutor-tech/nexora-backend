package com.robotutor.nexora.context.user.domain.aggregate

import com.robotutor.nexora.context.user.domain.event.UserEvent
import com.robotutor.nexora.context.user.domain.event.UserRegisteredEvent
import com.robotutor.nexora.context.user.domain.vo.Email
import com.robotutor.nexora.context.user.domain.vo.Mobile
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.UserId
import java.time.Instant

data class UserAggregate(
    val userId: UserId,
    val name: Name,
    val email: Email,
    val mobile: Mobile,
    val registeredAt: Instant = Instant.now(),
    val version: Long? = null
) : AggregateRoot<UserAggregate, UserId, UserEvent>(userId) {
    companion object {
        fun register(userId: UserId, name: Name, email: Email, mobile: Mobile): UserAggregate {
            val userAggregate = UserAggregate(userId = userId, name = name, email = email, mobile = mobile)
            userAggregate.addEvent(UserRegisteredEvent(userAggregate.userId))
            return userAggregate
        }
    }
}
