package com.robotutor.nexora.module.identity.domain.repository

import com.robotutor.nexora.module.identity.domain.aggregate.AccountAggregate
import com.robotutor.nexora.module.identity.domain.vo.CredentialId
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.principal.SubjectId
import reactor.core.publisher.Mono

interface AccountRepository {
    fun save(accountAggregate: AccountAggregate): Mono<AccountAggregate>
    fun findByCredentialId(credentialId: CredentialId): Mono<AccountAggregate>
    fun findByAccountId(accountId: AccountId): Mono<AccountAggregate>
    fun findByPrincipalId(subjectId: SubjectId): Mono<AccountAggregate>
    fun deleteByAccountId(accountId: AccountId): Mono<AccountAggregate>
    fun existsByCredentialId(credentialId: CredentialId): Mono<Boolean>
}
