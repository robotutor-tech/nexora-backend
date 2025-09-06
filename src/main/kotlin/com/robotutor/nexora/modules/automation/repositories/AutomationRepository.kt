//package com.robotutor.nexora.modules.automation.repositories
//
//import com.robotutor.nexora.modules.automation.models.AutomationId
//import com.robotutor.nexora.modules.automation.models.documents.AutomationDocument
//import com.robotutor.nexora.common.security.models.PremisesId
//import org.springframework.data.repository.reactive.ReactiveCrudRepository
//import org.springframework.stereotype.Repository
//import reactor.core.publisher.Flux
//import reactor.core.publisher.Mono
//
//@Repository
//interface AutomationRepository : ReactiveCrudRepository<AutomationDocument, AutomationId> {
//    fun findByAutomationIdAndPremisesId(automationId: AutomationId, premisesId: PremisesId): Mono<AutomationDocument>
//    fun findAllByAutomationIdInAndPremisesId(
//        automationIds: List<AutomationId>,
//        premisesId: PremisesId
//    ): Flux<AutomationDocument>
//}

