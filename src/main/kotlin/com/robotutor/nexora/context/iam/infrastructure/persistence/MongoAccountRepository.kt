package com.robotutor.nexora.context.iam.infrastructure.persistence

import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.context.iam.domain.event.IAMDomainEvent
import com.robotutor.nexora.context.iam.domain.repository.AccountRepository
import com.robotutor.nexora.context.iam.domain.vo.CredentialId
import com.robotutor.nexora.context.iam.domain.vo.CredentialKind
import com.robotutor.nexora.context.iam.infrastructure.messaging.IAMDomainEventPublisher
import com.robotutor.nexora.context.iam.infrastructure.persistence.mapper.AccountDocumentMapper
import com.robotutor.nexora.context.iam.infrastructure.persistence.repository.AccountDocumentRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.persistence.repository.retryOptimisticLockingFailure
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoAccountRepository(
    private val accountDocumentRepository: AccountDocumentRepository,
    private val eventPublisher: DomainEventPublisher<IAMDomainEvent>,
) : AccountRepository {
    override fun save(accountAggregate: AccountAggregate): Mono<AccountAggregate> {
        val accountDocument = AccountDocumentMapper.toMongoDocument(accountAggregate)
        return accountDocumentRepository.save(accountDocument)
            .retryOptimisticLockingFailure()
            .map { AccountDocumentMapper.toDomainModel(it) }
            .publishEvents(eventPublisher, accountAggregate)
    }

    override fun findByCredentialIdAndKind(credentialId: CredentialId, kind: CredentialKind): Mono<AccountAggregate> {
        return accountDocumentRepository.findByCredentials_CredentialIdAndCredentials_Kind(credentialId.value, kind)
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