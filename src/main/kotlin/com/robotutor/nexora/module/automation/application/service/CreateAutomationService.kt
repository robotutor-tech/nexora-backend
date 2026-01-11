package com.robotutor.nexora.module.automation.application.service

import com.robotutor.nexora.module.automation.application.command.CreateAutomationCommand
import com.robotutor.nexora.module.automation.application.resolver.DataResolver
import com.robotutor.nexora.module.automation.domain.aggregate.AutomationAggregate
import com.robotutor.nexora.module.automation.domain.exception.AutomationError
import com.robotutor.nexora.module.automation.domain.policy.CreateAutomationPolicy
import com.robotutor.nexora.module.automation.domain.repository.AutomationRepository
import com.robotutor.nexora.shared.domain.utility.enforcePolicy
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CreateAutomationService(
    private val createAutomationPolicy: CreateAutomationPolicy,
    private val automationRepository: AutomationRepository,
    private val dataResolver: DataResolver
) {
    fun execute(command: CreateAutomationCommand): Mono<AutomationAggregate> {
        return dataResolver.resolve(command)
            .enforcePolicy(createAutomationPolicy, { it }, AutomationError.NEXORA0301)
            .map {
                AutomationAggregate.register(
                    premisesId = command.premisesId,
                    name = command.name,
                    condition = command.condition,
                    triggers = command.triggers,
                    actions = command.actions,
                    executionMode = command.executionMode,
                )
            }
            .flatMap { automation -> automationRepository.save(automation) }
    }
}
