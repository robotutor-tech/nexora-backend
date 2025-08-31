package com.robotutor.nexora.modules.premises.infrastructure.persistence.repository

import com.robotutor.nexora.modules.premises.domain.model.Premises
import com.robotutor.nexora.modules.premises.domain.repository.PremisesRepository
import com.robotutor.nexora.modules.premises.infrastructure.persistence.mapper.PremisesDocumentMapper
import com.robotutor.nexora.modules.premises.infrastructure.persistence.document.PremisesDocument
import com.robotutor.nexora.shared.domain.model.PremisesId
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
) : MongoRepository<Premises, PremisesDocument>(mongoTemplate, PremisesDocument::class.java, PremisesDocumentMapper()),
    PremisesRepository {
    override fun save(premises: Premises): Mono<Premises> {
        val query = Query(Criteria.where("premisesId").`is`(premises.premisesId.value))
        return this.findAndReplace(query, premises)
    }

    override fun findAllByPremisesIdIn(premisesIds: List<PremisesId>): Flux<Premises> {
        val query = Query(Criteria.where("premisesId").`in`(premisesIds.map { it.value }))
        return this.findAll(query)
    }

    override fun findByPremisesId(premisesId: PremisesId): Mono<Premises> {
        val query = Query(Criteria.where("premisesId").`is`(premisesId.value))
        return this.findOne(query)
    }
}