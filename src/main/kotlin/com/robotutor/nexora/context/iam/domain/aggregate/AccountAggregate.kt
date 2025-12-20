package com.robotutor.nexora.context.iam.domain.aggregate

import com.robotutor.nexora.context.iam.domain.event.AccountActivatedEvent
import com.robotutor.nexora.context.iam.domain.event.AccountCreatedEvent
import com.robotutor.nexora.context.iam.domain.event.IAMDomainEvent
import com.robotutor.nexora.context.iam.domain.exception.NexoraError
import com.robotutor.nexora.context.iam.domain.vo.Credential
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.exception.InvalidStateException
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.AccountType
import java.time.Instant

class AccountAggregate private constructor(
    val accountId: AccountId,
    val type: AccountType,
    val credentials: List<Credential> = emptyList(),
    private var status: AccountStatus = AccountStatus.REGISTERED,
    val createdAt: Instant = Instant.now(),
    private var updatedAt: Instant = Instant.now(),
) : AggregateRoot<AccountAggregate, AccountId, IAMDomainEvent>(accountId) {

    fun getStatus(): AccountStatus = status
    fun getUpdatedAt(): Instant = updatedAt

    companion object {
        fun register(accountId: AccountId, type: AccountType, credentials: List<Credential>): AccountAggregate {
            val accountAggregate = create(accountId, type, credentials)
            accountAggregate.addEvent(AccountCreatedEvent(accountAggregate.accountId))
            return accountAggregate
        }

        fun create(
            accountId: AccountId,
            type: AccountType,
            credentials: List<Credential> = emptyList(),
            status: AccountStatus = AccountStatus.REGISTERED,
            createdAt: Instant = Instant.now(),
            updatedAt: Instant = Instant.now(),
        ): AccountAggregate {
            return AccountAggregate(
                accountId = accountId,
                type = type,
                credentials = credentials,
                status = status,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }

    fun activate(): AccountAggregate {
        if (status != AccountStatus.REGISTERED) {
            throw InvalidStateException(NexoraError.NEXORA0207)
        }
        this.status = AccountStatus.ACTIVE
        this.updatedAt = Instant.now()
        addEvent(AccountActivatedEvent(accountId))
        return this
    }
}

enum class AccountStatus {
    REGISTERED,
    ACTIVE,
    DISABLED
}