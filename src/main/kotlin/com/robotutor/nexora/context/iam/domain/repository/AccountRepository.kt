package com.robotutor.nexora.context.iam.domain.repository

import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.context.iam.domain.vo.CredentialId
import com.robotutor.nexora.shared.domain.vo.AccountId
import reactor.core.publisher.Mono

interface AccountRepository {
    fun save(accountAggregate: AccountAggregate): Mono<AccountAggregate>
    fun findByCredentialId(credentialId: CredentialId): Mono<AccountAggregate>
    fun findByAccountId(accountId: AccountId): Mono<AccountAggregate>
    fun deleteByAccountId(accountId: AccountId): Mono<AccountAggregate>
}