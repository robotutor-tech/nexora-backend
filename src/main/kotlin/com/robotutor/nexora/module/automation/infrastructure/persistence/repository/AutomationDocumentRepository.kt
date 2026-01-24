package com.robotutor.nexora.module.automation.infrastructure.persistence.repository

import com.robotutor.nexora.module.automation.infrastructure.persistence.document.AutomationDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface AutomationDocumentRepository : ReactiveCrudRepository<AutomationDocument, String> {
    fun findByAutomationIdAndPremisesId(automationId: String, premisesId: String): Mono<AutomationDocument>
    fun findByAutomationId(automationId: String): Mono<AutomationDocument>
}