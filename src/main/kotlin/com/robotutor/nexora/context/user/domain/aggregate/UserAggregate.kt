package com.robotutor.nexora.context.user.domain.aggregate

import com.robotutor.nexora.context.user.domain.event.UserEvent
import com.robotutor.nexora.context.user.domain.event.UserRegisteredEvent
import com.robotutor.nexora.context.user.domain.vo.Email
import com.robotutor.nexora.context.user.domain.vo.Mobile
import com.robotutor.nexora.context.user.domain.vo.UserId
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.Name
import java.time.Instant

class UserAggregate private constructor(
    val userId: UserId,
    val accountId: AccountId,
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
        fun register(accountId: AccountId, name: Name, email: Email, mobile: Mobile): UserAggregate {
            val userId = UserId.generate()
            val user = create(accountId = accountId, userId = userId, name = name, email = email, mobile = mobile)
            user.addEvent(UserRegisteredEvent(user.userId, user.accountId))
            return user
        }

        fun create(
            userId: UserId,
            name: Name,
            email: Email,
            mobile: Mobile,
            accountId: AccountId,
            state: UserState = UserState.ACTIVE,
            registeredAt: Instant = Instant.now(),
            updatedAt: Instant = Instant.now()
        ): UserAggregate {
            return UserAggregate(
                userId = userId,
                accountId = accountId,
                state = state,
                name = name,
                email = email,
                mobile = mobile,
                registeredAt = registeredAt,
                updatedAt = updatedAt
            )
        }
    }
}

enum class UserState {
    ACTIVE
}