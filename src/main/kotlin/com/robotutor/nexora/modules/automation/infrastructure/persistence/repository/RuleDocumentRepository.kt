package com.robotutor.nexora.modules.automation.infrastructure.persistence.repository

import com.robotutor.nexora.modules.automation.domain.entity.Rule
import com.robotutor.nexora.modules.automation.domain.entity.RuleId
import com.robotutor.nexora.modules.automation.domain.entity.RuleType
import com.robotutor.nexora.modules.automation.domain.entity.config.Config
import com.robotutor.nexora.modules.automation.domain.repository.RuleRepository
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.RuleDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.mapper.RuleDocumentMapper
import com.robotutor.nexora.modules.automation.infrastructure.persistence.mapper.config.ConfigDocumentMapper
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.infrastructure.persistence.repository.MongoRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class RuleDocumentRepository(
    mongoTemplate: ReactiveMongoTemplate,
) : MongoRepository<Rule, RuleDocument>(mongoTemplate, RuleDocument::class.java, RuleDocumentMapper),
    RuleRepository {
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
        return this.findOne(query)
    }

    override fun save(rule: Rule): Mono<Rule> {
        val query = Query(Criteria.where("ruleId").`is`(rule.ruleId.value))
        return this.findAndReplace(query, rule)
    }

    override fun findAllByPremisesIdAndRuleIdIn(premisesId: PremisesId, ruleIds: List<RuleId>): Flux<Rule> {
        val query = Query(
            Criteria.where("premisesId").`is`(premisesId.value)
                .and("ruleId").`in`(ruleIds.map { it.value })
        )
        return this.findAll(query)
    }

    override fun findByRuleIdAndPremisesId(ruleId: RuleId, premisesId: PremisesId): Mono<Rule> {
        val query = Query(
            Criteria.where("ruleId").`is`(ruleId.value)
                .and("premisesId").`is`(premisesId.value)
        )
        return this.findOne(query)
    }
}