package com.robotutor.nexora.context.iam.infrastructure.persistence.repository

import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.context.iam.domain.repository.AccountRepository
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.AccountDocument
import com.robotutor.nexora.context.iam.infrastructure.persistence.mapper.AccountDocumentMapper
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
}