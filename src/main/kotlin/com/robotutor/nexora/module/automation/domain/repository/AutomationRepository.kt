package com.robotutor.nexora.module.automation.domain.repository

import com.robotutor.nexora.module.automation.domain.aggregate.AutomationAggregate
import com.robotutor.nexora.module.automation.domain.vo.AutomationId
import com.robotutor.nexora.shared.domain.specification.Specification
import com.robotutor.nexora.shared.domain.vo.PremisesId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface AutomationRepository {
    fun save(automationAggregate: AutomationAggregate): Mono<AutomationAggregate>
    fun findAll(specification: Specification<AutomationAggregate>): Flux<AutomationAggregate>
    fun findByAutomationIdAndPremisesId(automationId: AutomationId, premisesId: PremisesId): Mono<AutomationAggregate>
    fun findByAutomationId(automationId: com.robotutor.nexora.module.automation.domain.vo.AutomationId): reactor.core.publisher.Mono<com.robotutor.nexora.module.automation.domain.aggregate.AutomationAggregate>
}