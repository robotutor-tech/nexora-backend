package com.robotutor.nexora.modules.automation.infrastructure.persistence.repository

import com.robotutor.nexora.modules.automation.domain.entity.Action
import com.robotutor.nexora.modules.automation.domain.entity.ActionId
import com.robotutor.nexora.modules.automation.domain.entity.config.ActionConfig
import com.robotutor.nexora.modules.automation.domain.repository.ActionRepository
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.ActionDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.mapper.ActionDocumentMapper
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.infrastructure.persistence.repository.MongoRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class ActionDocumentRepository(
    mongoTemplate: ReactiveMongoTemplate,
    val mapper: ActionDocumentMapper
) : MongoRepository<Action, ActionDocument>(mongoTemplate, ActionDocument::class.java, mapper), ActionRepository {
    override fun findByPremisesIdAndConfig(premisesId: PremisesId, config: ActionConfig): Mono<Action> {
        val query = Query(
            Criteria.where("premisesId").`is`(premisesId.value)
                .and("config").`is`(mapper.toConfigDocument(config))
        )
        return this.findOne(query)
    }

    override fun save(action: Action): Mono<Action> {
        val query = Query(Criteria.where("actionId").`is`(action.actionId.value))
        return this.findAndReplace(query, action)
    }

    override fun findAllByPremisesIdAndActionIdIn(premisesId: PremisesId, actionsIds: List<ActionId>): Flux<Action> {
        val query = Query(
            Criteria.where("premisesId").`is`(premisesId.value)
                .and("actionId").`in`(actionsIds.map { it.value })
        )
        return this.findAll(query)
    }

    override fun findByActionIdAndPremisesId(actionId: ActionId, premisesId: PremisesId): Mono<Action> {
        val query = Query(
            Criteria.where("actionId").`is`(actionId.value)
                .and("premisesId").`is`(premisesId.value)
        )
        return this.findOne(query)
    }
}