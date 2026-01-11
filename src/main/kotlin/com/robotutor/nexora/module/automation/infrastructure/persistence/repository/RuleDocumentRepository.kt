package com.robotutor.nexora.module.automation.infrastructure.persistence.repository

import com.robotutor.nexora.module.automation.domain.entity.Rule
import com.robotutor.nexora.module.automation.domain.entity.RuleId
import com.robotutor.nexora.module.automation.domain.entity.RuleType
import com.robotutor.nexora.module.automation.domain.entity.config.Config
import com.robotutor.nexora.module.automation.domain.repository.RuleRepository
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.RuleDocument
import com.robotutor.nexora.module.automation.infrastructure.persistence.mapper.RuleDocumentMapper
import com.robotutor.nexora.module.automation.infrastructure.persistence.mapper.config.ConfigDocumentMapper
import com.robotutor.nexora.shared.domain.vo.PremisesId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class RuleDocumentRepository(
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
) : RuleRepository {
    override fun findByTypeAndPremisesIdAndConfig(
        type: RuleType,
        premisesId: PremisesId,
        config: Config
    ): Mono<Rule> {
        val query = Query(
            Criteria.where("premisesId").`is`(premisesId.value)
                .and("config").`is`(ConfigDocumentMapper.toConfigDocument(config))
                .and("type").`is`(type)
        )
        return reactiveMongoTemplate.findOne<RuleDocument>(query)
            .map { RuleDocumentMapper.toDomainModel(it) }
    }

    override fun save(rule: Rule): Mono<Rule> {
        val query = Query(Criteria.where("ruleId").`is`(rule.ruleId.value))
        val ruleDocument = RuleDocumentMapper.toMongoDocument(rule)
        return reactiveMongoTemplate.findAndReplace(query, ruleDocument)
            .map { RuleDocumentMapper.toDomainModel(it) }
    }

    override fun findAllByPremisesIdAndRuleIdIn(premisesId: PremisesId, ruleIds: List<RuleId>): Flux<Rule> {
        val query = Query(
            Criteria.where("premisesId").`is`(premisesId.value)
                .and("ruleId").`in`(ruleIds.map { it.value })
        )
        return reactiveMongoTemplate.find<RuleDocument>(query)
            .map { RuleDocumentMapper.toDomainModel(it) }
    }

    override fun findByRuleIdAndPremisesId(ruleId: RuleId, premisesId: PremisesId): Mono<Rule> {
        val query = Query(
            Criteria.where("ruleId").`is`(ruleId.value)
                .and("premisesId").`is`(premisesId.value)
        )
        return reactiveMongoTemplate.findOne<RuleDocument>(query)
            .map { RuleDocumentMapper.toDomainModel(it) }
    }
}