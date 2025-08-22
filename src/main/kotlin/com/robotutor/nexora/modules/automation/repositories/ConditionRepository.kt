//package com.robotutor.nexora.modules.automation.repositories
//
//import com.robotutor.nexora.modules.automation.models.ConditionId
//import com.robotutor.nexora.modules.automation.models.documents.ConditionDocument
//import com.robotutor.nexora.common.security.models.PremisesId
//import org.springframework.data.repository.reactive.ReactiveCrudRepository
//import org.springframework.stereotype.Repository
//import reactor.core.publisher.Flux
//import reactor.core.publisher.Mono
//
//@Repository
//interface ConditionRepository : ReactiveCrudRepository<ConditionDocument, ConditionId> {
//    fun findByConditionIdAndPremisesId(conditionId: ConditionId, premisesId: PremisesId): Mono<ConditionDocument>
//    fun findByPremisesIdAndConfig(premisesId: PremisesId, config: Map<String, Any?>): Mono<ConditionDocument>
//    fun findAllByConditionIdInAndPremisesId(
//        conditionIds: List<ConditionId>,
//        premisesId: PremisesId
//    ): Flux<ConditionDocument>
//}
