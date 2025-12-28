package com.robotutor.nexora.context.iam.infrastructure.persistence

import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.context.iam.domain.event.IAMEventPublisher
import com.robotutor.nexora.context.iam.domain.repository.AccountRepository
import com.robotutor.nexora.context.iam.domain.vo.CredentialId
import com.robotutor.nexora.context.iam.infrastructure.persistence.mapper.AccountDocumentMapper
import com.robotutor.nexora.context.iam.infrastructure.persistence.repository.AccountDocumentRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.common.persistence.repository.retryOptimisticLockingFailure
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoAccountRepository(
    private val accountDocumentRepository: AccountDocumentRepository,
    private val eventPublisher: IAMEventPublisher,
) : AccountRepository {
    override fun save(accountAggregate: AccountAggregate): Mono<AccountAggregate> {
        val accountDocument = AccountDocumentMapper.toMongoDocument(accountAggregate)
        return accountDocumentRepository.save(accountDocument)
            .retryOptimisticLockingFailure()
            .map { AccountDocumentMapper.toDomainModel(it) }
            .publishEvents(eventPublisher, accountAggregate)
    }

    override fun findByCredentialId(credentialId: CredentialId): Mono<AccountAggregate> {
        return accountDocumentRepository.findByCredentials_CredentialId(credentialId.value)
            .map { AccountDocumentMapper.toDomainModel(it) }
    }

    override fun findByAccountId(accountId: AccountId): Mono<AccountAggregate> {
        return accountDocumentRepository.findByAccountId(accountId.value)
            .map { AccountDocumentMapper.toDomainModel(it) }
    }

    override fun deleteByAccountId(accountId: AccountId): Mono<AccountAggregate> {
        return accountDocumentRepository.deleteByAccountId(accountId.value)
            .map { AccountDocumentMapper.toDomainModel(it) }
    }
}