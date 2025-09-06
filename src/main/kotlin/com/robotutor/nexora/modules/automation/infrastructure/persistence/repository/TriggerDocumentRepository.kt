package com.robotutor.nexora.modules.automation.infrastructure.persistence.repository

import com.robotutor.nexora.modules.automation.domain.entity.Trigger
import com.robotutor.nexora.modules.automation.domain.entity.TriggerId
import com.robotutor.nexora.modules.automation.domain.entity.config.TriggerConfig
import com.robotutor.nexora.modules.automation.domain.repository.TriggerRepository
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.TriggerDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.mapper.TriggerDocumentMapper
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.infrastructure.persistence.repository.MongoRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class TriggerDocumentRepository(
    mongoTemplate: ReactiveMongoTemplate,
    val mapper: TriggerDocumentMapper
) : MongoRepository<Trigger, TriggerDocument>(mongoTemplate, TriggerDocument::class.java, mapper), TriggerRepository {
    override fun findByPremisesIdAndConfig(premisesId: PremisesId, config: TriggerConfig): Mono<Trigger> {
        val query = Query(
            Criteria.where("premisesId").`is`(premisesId.value)
                .and("config").`is`(mapper.toConfigDocument(config))
        )
        return this.findOne(query)
    }

    override fun save(trigger: Trigger): Mono<Trigger> {
        val query = Query(Criteria.where("triggerId").`is`(trigger.triggerId.value))
        return this.findAndReplace(query, trigger)
    }

    override fun findAllByPremisesIdAndTriggerIdIn(premisesId: PremisesId, triggersIds: List<TriggerId>): Flux<Trigger> {
        val query = Query(
            Criteria.where("premisesId").`is`(premisesId.value)
                .and("triggerId").`in`(triggersIds.map { it.value })
        )
        return this.findAll(query)
    }

    override fun findByTriggerIdAndPremisesId(triggerId: TriggerId, premisesId: PremisesId): Mono<Trigger> {
        val query = Query(
            Criteria.where("triggerId").`is`(triggerId.value)
                .and("premisesId").`is`(premisesId.value)
        )
        return this.findOne(query)
    }
}