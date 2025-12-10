package com.robotutor.nexora.context.iam.domain.aggregate

import com.robotutor.nexora.context.iam.domain.event.AccountCreatedEvent
import com.robotutor.nexora.context.iam.domain.event.IAMEvent
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.context.iam.domain.vo.Credential
import com.robotutor.nexora.shared.domain.AggregateRoot
import java.time.Instant

data class AccountAggregate(
    val accountId: AccountId,
    val type: AccountType,
    val credentials: List<Credential> = emptyList(),
    val status: AccountStatus = AccountStatus.ACTIVE,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val version: Long = 0
) : AggregateRoot<AccountAggregate, AccountId, IAMEvent>(accountId) {
    companion object {
        fun register(accountId: AccountId, type: AccountType, credentials: List<Credential>): AccountAggregate {
            val accountAggregate = AccountAggregate(accountId, type, credentials)
            accountAggregate.addEvent(AccountCreatedEvent(accountAggregate))
            return accountAggregate
        }
    }
}

enum class AccountType { HUMAN, MACHINE }

enum class AccountStatus {
    ACTIVE,
    DISABLED
}