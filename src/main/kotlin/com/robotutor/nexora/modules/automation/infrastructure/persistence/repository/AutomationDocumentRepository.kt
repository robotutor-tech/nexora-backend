package com.robotutor.nexora.modules.automation.infrastructure.persistence.repository

import com.robotutor.nexora.modules.automation.domain.entity.Automation
import com.robotutor.nexora.modules.automation.domain.entity.AutomationId
import com.robotutor.nexora.modules.automation.domain.repository.AutomationRepository
import com.robotutor.nexora.shared.domain.vo.PremisesId
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AutomationDocumentRepository : AutomationRepository {
    override fun findByAutomationIdAndPremisesId(automationId: AutomationId, premisesId: PremisesId): Mono<Automation> {
        return Mono.empty()
    }
}