package com.robotutor.nexora.modules.automation.domain.repository

import com.robotutor.nexora.modules.automation.domain.entity.Rule
import com.robotutor.nexora.modules.automation.domain.entity.RuleId
import com.robotutor.nexora.modules.automation.domain.entity.RuleType
import com.robotutor.nexora.modules.automation.domain.entity.config.Config
import com.robotutor.nexora.shared.domain.vo.PremisesId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RuleRepository {
    fun findByTypeAndPremisesIdAndConfig(type: RuleType, premisesId: PremisesId, config: Config): Mono<Rule>
    fun save(rule: Rule): Mono<Rule>
    fun findAllByPremisesIdAndRuleIdIn(premisesId: PremisesId, ruleIds: List<RuleId>): Flux<Rule>
    fun findByRuleIdAndPremisesId(ruleId: RuleId, premisesId: PremisesId): Mono<Rule>
}