package com.robotutor.nexora.module.automation.application.service

import com.robotutor.nexora.module.automation.application.command.ExecuteAutomationCommand
import com.robotutor.nexora.module.automation.application.resolver.DataResolver
import com.robotutor.nexora.module.automation.domain.aggregate.AutomationAggregate
import com.robotutor.nexora.module.automation.domain.repository.AutomationRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ExecuteAutomationService(
    private val automationRepository: AutomationRepository,
    private val dataResolver: DataResolver
) {
    fun execute(command: ExecuteAutomationCommand): Mono<AutomationAggregate> {
        return automationRepository.findByAutomationId(command.automationId)
            .flatMap { dataResolver.resolve(it) }
    }
}
