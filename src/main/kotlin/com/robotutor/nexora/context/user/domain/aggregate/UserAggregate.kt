package com.robotutor.nexora.context.user.domain.aggregate

import com.robotutor.nexora.context.user.domain.event.UserActivatedEvent
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.context.user.domain.event.UserDomainEvent
import com.robotutor.nexora.context.user.domain.event.UserRegisteredEvent
import com.robotutor.nexora.context.user.domain.exception.NexoraError
import com.robotutor.nexora.context.user.domain.vo.Email
import com.robotutor.nexora.context.user.domain.vo.Mobile
import com.robotutor.nexora.context.user.domain.vo.UserId
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.exception.InvalidStateException
import com.robotutor.nexora.shared.domain.vo.Name
import java.time.Instant

class UserAggregate private constructor(
    val userId: UserId,
    private var accountId: AccountId? = null,
    private var state: UserState,
    val name: Name,
    val email: Email,
    val mobile: Mobile,
    val registeredAt: Instant,
    private var updatedAt: Instant,
) : AggregateRoot<UserAggregate, UserId, UserDomainEvent>(userId) {
    init {
        validate()
    }

    fun state(): UserState = state
    fun accountId(): AccountId? = accountId
    fun updatedAt(): Instant = updatedAt

    companion object {
        fun register(name: Name, email: Email, mobile: Mobile): UserAggregate {
            val userAggregate = create(userId = UserId.generate(), name = name, email = email, mobile = mobile)
            userAggregate.addEvent(UserRegisteredEvent(userAggregate.userId))
            return userAggregate
        }

        fun create(
            userId: UserId,
            name: Name,
            email: Email,
            mobile: Mobile,
            accountId: AccountId? = null,
            state: UserState = UserState.REGISTERED,
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

    fun activate(accountId: AccountId): UserAggregate {
        if (state != UserState.REGISTERED) {
            throw InvalidStateException(NexoraError.NEXORA0204)
        }
        this.accountId = accountId
        state = UserState.ACTIVE
        updatedAt = Instant.now()
        addEvent(UserActivatedEvent(userId, accountId))
        return this
    }

    private fun validate() {
        if (accountId == null && state != UserState.REGISTERED) {
            throw InvalidStateException(NexoraError.NEXORA0202)
        }
        if (accountId != null && state == UserState.REGISTERED) {
            throw InvalidStateException(NexoraError.NEXORA0203)
        }
    }
}

enum class UserState {
    REGISTERED, ACTIVE
}