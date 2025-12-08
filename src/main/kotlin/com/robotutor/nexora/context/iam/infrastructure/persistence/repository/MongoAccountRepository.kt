package com.robotutor.nexora.context.iam.infrastructure.persistence.repository

import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.context.iam.domain.repository.AccountRepository
import com.robotutor.nexora.context.iam.domain.vo.CredentialId
import com.robotutor.nexora.context.iam.domain.vo.CredentialKind
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.AccountDocument
import com.robotutor.nexora.context.iam.infrastructure.persistence.mapper.AccountDocumentMapper
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.infrastructure.persistence.repository.MongoRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoAccountRepository(
    mongoTemplate: ReactiveMongoTemplate
) : MongoRepository<AccountAggregate, AccountDocument>(
    mongoTemplate, AccountDocument::class.java,
    AccountDocumentMapper
), AccountRepository {
    override fun save(accountAggregate: AccountAggregate): Mono<AccountAggregate> {
        val query = Query(Criteria.where("accountId").`is`(accountAggregate.accountId.value))
        return this.findAndReplace(query, accountAggregate)
    }

    override fun findByCredentialIdAndKind(credentialId: CredentialId, kind: CredentialKind): Mono<AccountAggregate> {
        val query = Query(
            Criteria.where("credentials").elemMatch(
                Criteria.where("credentialId").`is`(credentialId.value)
                    .and("kind").`is`(kind.name)
            )
        )
        return this.findOne(query)
    }

    override fun findByAccountId(accountId: AccountId): Mono<AccountAggregate> {
        val query = Query(Criteria.where("accountId").`is`(accountId.value))
        return this.findOne(query)
    }
}