package com.robotutor.nexora.context.user.domain.aggregate

import com.robotutor.nexora.context.user.domain.event.UserActivatedEvent
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.context.user.domain.event.UserEvent
import com.robotutor.nexora.context.user.domain.event.UserRegisteredEvent
import com.robotutor.nexora.context.user.domain.exception.NexoraError
import com.robotutor.nexora.context.user.domain.vo.Email
import com.robotutor.nexora.context.user.domain.vo.Mobile
import com.robotutor.nexora.context.user.domain.vo.UserId
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.exception.InvalidStateException
import com.robotutor.nexora.shared.domain.vo.Name
import java.time.Instant

data class UserAggregate(
    val userId: UserId,
    val accountId: AccountId? = null,
    val state: UserState = UserState.REGISTERED,
    val name: Name,
    val email: Email,
    val mobile: Mobile,
    val registeredAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val version: Long? = null
) : AggregateRoot<UserAggregate, UserId, UserEvent>(userId) {

    init {
        validate()
    }

    companion object {
        fun register(name: Name, email: Email, mobile: Mobile): UserAggregate {
            val userAggregate = UserAggregate(userId = UserId.generate(), name = name, email = email, mobile = mobile)
            userAggregate.addEvent(UserRegisteredEvent(userAggregate.userId))
            return userAggregate
        }
    }

    fun activate(accountId: AccountId): UserAggregate {
        if (state != UserState.REGISTERED) {
            throw InvalidStateException(NexoraError.NEXORA0204)
        }
        val updated = copy(accountId = accountId, state = UserState.ACTIVE, updatedAt = Instant.now())
        updated.addEvent(UserActivatedEvent(updated.userId, updated.accountId!!))
        return updated
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