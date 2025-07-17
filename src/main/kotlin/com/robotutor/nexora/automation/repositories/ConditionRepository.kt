package com.robotutor.nexora.automation.repositories

import com.robotutor.nexora.automation.models.ConditionId
import com.robotutor.nexora.automation.models.documents.ConditionDocument
import com.robotutor.nexora.premises.models.PremisesId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface ConditionRepository : ReactiveCrudRepository<ConditionDocument, ConditionId> {
    fun findByConditionIdAndPremisesId(conditionId: ConditionId, premisesId: PremisesId): Mono<ConditionDocument>
    fun findByPremisesIdAndConfig(premisesId: PremisesId, config: Map<String, Any?>): Mono<ConditionDocument>
    fun findAllByConditionIdInAndPremisesId(
        conditionIds: List<ConditionId>,
        premisesId: PremisesId
    ): Flux<ConditionDocument>
}
