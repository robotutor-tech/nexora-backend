package com.robotutor.nexora.module.automation.application.service

import com.robotutor.nexora.module.automation.application.command.ExecuteAutomationCommand
import com.robotutor.nexora.module.automation.application.executor.AutomationExecutionResult
import com.robotutor.nexora.module.automation.application.executor.AutomationExecutor
import com.robotutor.nexora.module.automation.application.resolver.DataResolver
import com.robotutor.nexora.module.automation.domain.exception.AutomationError
import com.robotutor.nexora.module.automation.domain.policy.ExecuteAutomationPolicy
import com.robotutor.nexora.module.automation.domain.repository.AutomationRepository
import com.robotutor.nexora.shared.domain.utility.enforcePolicy
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ExecuteAutomationService(
    private val automationRepository: AutomationRepository,
    private val dataResolver: DataResolver,
    private val executeAutomationPolicy: ExecuteAutomationPolicy,
    private val automationExecutor: AutomationExecutor,
) {
    fun execute(command: ExecuteAutomationCommand): Mono<AutomationExecutionResult> {
        return automationRepository.findByAutomationId(command.automationId)
            .flatMap { automation -> dataResolver.resolve(automation) }
            .enforcePolicy(executeAutomationPolicy, { it }, AutomationError.NEXORA0301)
            .flatMap { resolved -> automationExecutor.execute(resolved) }
    }
}
