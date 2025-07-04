package com.robotutor.nexora.automation.repositories

import com.robotutor.nexora.automation.models.Automation
import com.robotutor.nexora.automation.models.AutomationId
import com.robotutor.nexora.premises.models.PremisesId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface AutomationRepository : ReactiveCrudRepository<Automation, AutomationId> {
    fun findByAutomationIdAndPremisesId(automationId: AutomationId, premisesId: PremisesId): Mono<Automation>
}
