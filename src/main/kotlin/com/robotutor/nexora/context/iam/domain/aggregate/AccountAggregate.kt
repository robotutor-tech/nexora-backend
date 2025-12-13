package com.robotutor.nexora.context.iam.domain.aggregate

import com.robotutor.nexora.context.iam.domain.event.AccountCreatedEvent
import com.robotutor.nexora.context.iam.domain.event.IAMDomainEvent
import com.robotutor.nexora.context.iam.domain.vo.Credential
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.AccountType
import java.time.Instant

data class AccountAggregate(
    val accountId: AccountId,
    val type: AccountType,
    val credentials: List<Credential> = emptyList(),
    val status: AccountStatus = AccountStatus.ACTIVE,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
) : AggregateRoot<AccountAggregate, AccountId, IAMDomainEvent>(accountId) {
    companion object {
        fun register(accountId: AccountId, type: AccountType, credentials: List<Credential>): AccountAggregate {
            val accountAggregate = AccountAggregate(accountId, type, credentials)
            accountAggregate.addEvent(AccountCreatedEvent(accountAggregate))
            return accountAggregate
        }
    }
}

enum class AccountStatus {
    ACTIVE,
    DISABLED
}