package com.robotutor.nexora.module.automation.application.resolver.strategy

import com.robotutor.nexora.module.automation.application.resolver.ResolverStrategy
import com.robotutor.nexora.module.automation.domain.aggregate.AutomationAggregate
import com.robotutor.nexora.shared.utility.createMonoError
import com.robotutor.nexora.module.automation.domain.repository.AutomationRepository
import com.robotutor.nexora.module.automation.domain.exception.AutomationError
import com.robotutor.nexora.module.automation.domain.vo.component.Automation
import com.robotutor.nexora.module.automation.domain.vo.component.data.AutomationData
import com.robotutor.nexora.shared.domain.exception.BadDataException
import com.robotutor.nexora.shared.domain.exception.ErrorResponse
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AutomationResolverStrategy(private val automationRepository: AutomationRepository) :
    ResolverStrategy<Automation, AutomationData> {
    override fun resolve(component: Automation): Mono<AutomationData> {
        return automationRepository.findByAutomationId(component.automationId)
            .map { AutomationData(it.automationId) }
    }
}