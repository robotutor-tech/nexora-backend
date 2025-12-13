package com.robotutor.nexora.modules.automation.domain.repository

import com.robotutor.nexora.modules.automation.domain.entity.Automation
import com.robotutor.nexora.modules.automation.domain.entity.AutomationId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import reactor.core.publisher.Mono

interface AutomationRepository {
    fun findByAutomationIdAndPremisesId(automationId: AutomationId, premisesId: PremisesId): Mono<Automation>
}