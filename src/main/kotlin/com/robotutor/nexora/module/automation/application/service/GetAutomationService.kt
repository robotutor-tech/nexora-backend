package com.robotutor.nexora.module.automation.application.service

import com.robotutor.nexora.module.automation.application.command.GetAutomationsQuery
import com.robotutor.nexora.module.automation.domain.aggregate.AutomationAggregate
import com.robotutor.nexora.module.automation.domain.repository.AutomationRepository
import com.robotutor.nexora.module.automation.domain.specification.AutomationByPremisesIdSpecification
import com.robotutor.nexora.shared.domain.specification.ResourceSpecificationBuilder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class GetAutomationService(
    private val resourceSpecificationBuilder: ResourceSpecificationBuilder<AutomationAggregate>,
    private val automationRepository: AutomationRepository,
) {
    fun execute(query: GetAutomationsQuery): Flux<AutomationAggregate> {
        val specification = resourceSpecificationBuilder.build(query.resources)
            .and(AutomationByPremisesIdSpecification(query.resources.premisesId))
        return automationRepository.findAll(specification)

    }
}
