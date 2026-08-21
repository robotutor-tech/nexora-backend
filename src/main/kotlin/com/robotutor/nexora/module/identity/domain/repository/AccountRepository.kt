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
    fun existsByCredentialId(credentialId: CredentialId): Mono<Boolean>
    fun findByAccountId(accountId: AccountId): Mono<Account>
    fun findBySubjectId(subjectId: SubjectId): Mono<Account>
    fun deleteByAccountId(accountId: AccountId): Mono<Account>
    fun findAll(specification: Specification<Account>): Flux<Account>
}
