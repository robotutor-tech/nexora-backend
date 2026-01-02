package com.robotutor.nexora.module.user.domain.aggregate

import com.robotutor.nexora.module.user.domain.event.UserActivatedEvent
import com.robotutor.nexora.module.user.domain.event.UserEvent
import com.robotutor.nexora.module.user.domain.event.UserRegisteredEvent
import com.robotutor.nexora.module.user.domain.exception.UserError
import com.robotutor.nexora.module.user.domain.vo.Email
import com.robotutor.nexora.module.user.domain.vo.Mobile
import com.robotutor.nexora.module.user.domain.vo.UserId
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.exception.InvalidStateException
import com.robotutor.nexora.shared.domain.vo.Name
import java.time.Instant

class UserAggregate private constructor(
    val userId: UserId,
    val name: Name,
    val email: Email,
    val mobile: Mobile,
    val registeredAt: Instant,
    private var state: UserState,
    private var updatedAt: Instant,
) : AggregateRoot<UserAggregate, UserId, UserEvent>(userId) {

    fun state(): UserState = state
    fun updatedAt(): Instant = updatedAt

    companion object {
        fun register(name: Name, email: Email, mobile: Mobile): UserAggregate {
            val userId = UserId.generate()
            val user = create(userId = userId, name = name, email = email, mobile = mobile)
            user.addEvent(UserRegisteredEvent(user.userId))
            return user
        }

        fun create(
            userId: UserId,
            name: Name,
            email: Email,
            mobile: Mobile,
            state: UserState = UserState.REGISTERED,
            registeredAt: Instant = Instant.now(),
            updatedAt: Instant = Instant.now()
        ): UserAggregate {
            return UserAggregate(
                userId = userId,
                state = state,
                name = name,
                email = email,
                mobile = mobile,
                registeredAt = registeredAt,
                updatedAt = updatedAt
            )
        }
    }

    fun activate(): UserAggregate {
        if (state != UserState.REGISTERED) {
            throw InvalidStateException(UserError.NEXORA0202)
        }
        state = UserState.ACTIVE
        updatedAt = Instant.now()
        addEvent(UserActivatedEvent(userId))
        return this
    }
}

enum class UserState {
    REGISTERED,
    ACTIVE
}