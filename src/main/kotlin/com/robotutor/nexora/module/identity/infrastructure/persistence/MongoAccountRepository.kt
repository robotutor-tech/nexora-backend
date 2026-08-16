package com.robotutor.nexora.module.identity.infrastructure.persistence

import com.robotutor.nexora.module.identity.domain.aggregate.Account
import com.robotutor.nexora.module.identity.domain.repository.AccountRepository
import com.robotutor.nexora.module.identity.domain.vo.CredentialId
import com.robotutor.nexora.module.identity.infrastructure.messaging.mapper.IdentityEventMapper
import com.robotutor.nexora.module.identity.infrastructure.persistence.mapper.AccountDocumentMapper
import com.robotutor.nexora.module.identity.infrastructure.persistence.repository.AccountDocumentRepository
import com.robotutor.nexora.shared.domain.specification.Specification
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.SubjectId
import com.robotutor.nexora.shared.outbox.publishEvents
import com.robotutor.nexora.shared.persistence.repository.retryOptimisticLockingFailure
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class MongoAccountRepository(
    private val accountDocumentRepository: AccountDocumentRepository,
) : AccountRepository {
    override fun save(account: Account): Mono<Account> {
        val accountDocument = AccountDocumentMapper.toMongoDocument(account)
        return accountDocumentRepository.save(accountDocument)
            .retryOptimisticLockingFailure()
            .map { AccountDocumentMapper.toDomainModel(it) }
            .publishEvents(account, IdentityEventMapper)
    }

    override fun findByCredentialId(credentialId: CredentialId): Mono<Account> {
        return accountDocumentRepository.findByCredential_CredentialId(credentialId.value)
            .map { AccountDocumentMapper.toDomainModel(it) }
    }

    override fun findByAccountId(accountId: AccountId): Mono<Account> {
        return accountDocumentRepository.findByAccountId(accountId.value)
            .map { AccountDocumentMapper.toDomainModel(it) }
    }

    override fun findBySubjectId(subjectId: SubjectId): Mono<Account> {
        return accountDocumentRepository.findBySubjectId(subjectId.value)
            .map { AccountDocumentMapper.toDomainModel(it) }
    }

    override fun deleteByAccountId(accountId: AccountId): Mono<Account> {
        return accountDocumentRepository.deleteByAccountId(accountId.value)
            .map { AccountDocumentMapper.toDomainModel(it) }
    }

    override fun findAll(specification: Specification<Account>): Flux<Account> {
        return Flux.empty<Account>()
    }
}
