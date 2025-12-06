package com.robotutor.nexora.context.iam.domain.repository

import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.context.iam.domain.vo.CredentialId
import com.robotutor.nexora.context.iam.domain.vo.CredentialKind
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface AccountRepository {
    fun save(accountAggregate: AccountAggregate): Mono<AccountAggregate>
    fun findByCredentialIdAndKind(credentialId: CredentialId, kind: CredentialKind): Mono<AccountAggregate>
}