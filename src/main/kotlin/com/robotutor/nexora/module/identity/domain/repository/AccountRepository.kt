package com.robotutor.nexora.module.identity.domain.repository

import com.robotutor.nexora.module.identity.domain.aggregate.Account
import com.robotutor.nexora.module.identity.domain.vo.CredentialId
import com.robotutor.nexora.shared.domain.specification.Specification
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.SubjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface AccountRepository {
    fun save(account: Account): Mono<Account>
    fun findByCredentialId(credentialId: CredentialId): Mono<Account>
    fun findByAccountId(accountId: AccountId): Mono<Account>
    fun findByPrincipalId(subjectId: SubjectId): Mono<Account>
    fun deleteByAccountId(accountId: AccountId): Mono<Account>
    fun findAll(specification: Specification<Account>): Flux<Account>
}
