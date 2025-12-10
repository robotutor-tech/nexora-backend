package com.robotutor.nexora.context.iam.infrastructure.persistence.repository

import com.robotutor.nexora.context.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.context.iam.domain.repository.ActorRepository
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.ActorDocument
import com.robotutor.nexora.context.iam.infrastructure.persistence.mapper.ActorDocumentMapper
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.infrastructure.persistence.repository.MongoRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MongoActorRepository(
    mongoTemplate: ReactiveMongoTemplate,
) : MongoRepository<ActorAggregate, ActorDocument>(mongoTemplate, ActorDocument::class.java, ActorDocumentMapper),
    ActorRepository {
    override fun save(actorAggregate: ActorAggregate): Mono<ActorAggregate> {
        val query = Query(Criteria.where("actorId").`is`(actorAggregate.actorId.value))
        return this.findAndReplace(query, actorAggregate)
    }

    override fun findAllByAccountId(accountId: AccountId): Flux<ActorAggregate> {
        val query = Query(Criteria.where("accountId").`is`(accountId.value))
        return this.findAll(query)
    }

    override fun findByAccountIdAndPremisesId(
        accountId: AccountId,
        premisesId: PremisesId
    ): Mono<ActorAggregate> {
        val query = Query(
            Criteria.where("accountId").`is`(accountId.value)
                .and("premisesId").`is`(premisesId.value)
        )
        return this.findOne(query)
    }
}