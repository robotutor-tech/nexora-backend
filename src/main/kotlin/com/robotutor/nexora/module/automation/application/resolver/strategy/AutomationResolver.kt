package com.robotutor.nexora.module.automation.application.resolver.strategy

import com.robotutor.nexora.module.automation.domain.repository.AutomationRepository
import com.robotutor.nexora.module.automation.domain.vo.component.AutomationComponent
import com.robotutor.nexora.module.automation.domain.vo.component.data.AutomationData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AutomationResolver(
    private val automationRepository: AutomationRepository
) : ComponentResolver<AutomationComponent, AutomationData> {
    override fun resolve(component: AutomationComponent): Mono<AutomationData> {
        return automationRepository.findByAutomationId(component.automationId)
            .map { AutomationData(it.automationId) }
    }
}