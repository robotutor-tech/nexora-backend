package com.robotutor.nexora.modules.automation.application

import com.robotutor.nexora.modules.automation.application.command.CreateAutomationCommand
import com.robotutor.nexora.modules.automation.domain.entity.Automation
import com.robotutor.nexora.modules.automation.domain.entity.AutomationId
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AutomationUseCase {
    fun createAutomationRule(createAutomationCommand: CreateAutomationCommand, actorData: ActorData) : Mono<Automation> {
        TODO("Not yet implemented")
    }

    fun getAutomationRules(automationIds: List<AutomationId>, actorData: ActorData): Flux<Automation> {
        TODO("Not yet implemented")
    }
}
