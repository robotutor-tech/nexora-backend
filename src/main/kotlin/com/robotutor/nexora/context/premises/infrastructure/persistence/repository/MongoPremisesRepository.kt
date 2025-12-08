package com.robotutor.nexora.context.premises.infrastructure.persistence.repository

import com.robotutor.nexora.context.premises.domain.aggregate.PremisesAggregate
import com.robotutor.nexora.context.premises.domain.repository.PremisesRepository
import com.robotutor.nexora.context.premises.infrastructure.persistence.document.PremisesDocument
import com.robotutor.nexora.context.premises.infrastructure.persistence.mapper.PremisesDocumentMapper
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.infrastructure.persistence.repository.MongoRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MongoPremisesRepository(
    mongoTemplate: ReactiveMongoTemplate,
) : MongoRepository<PremisesAggregate, PremisesDocument>(
    mongoTemplate,
    PremisesDocument::class.java,
    PremisesDocumentMapper
), PremisesRepository {
    override fun save(premisesAggregate: PremisesAggregate): Mono<PremisesAggregate> {
        val query = Query(Criteria.where("premisesId").`is`(premisesAggregate.premisesId.value))
        return this.findAndReplace(query, premisesAggregate)
    }

    override fun findAllByPremisesIdIn(premisesIds: List<PremisesId>): Flux<PremisesAggregate> {
        val query = Query(Criteria.where("premisesId").`in`(premisesIds.map { it.value }))
        return this.findAll(query)
    }

    override fun findByPremisesId(premisesId: PremisesId): Mono<PremisesAggregate> {
        val query = Query(Criteria.where("premisesId").`is`(premisesId.value))
        return this.findOne(query)
    }
}