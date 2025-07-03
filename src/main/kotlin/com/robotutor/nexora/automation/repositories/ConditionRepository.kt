package com.robotutor.nexora.automation.repositories

import com.robotutor.nexora.automation.models.Condition
import com.robotutor.nexora.automation.models.ConditionId
import com.robotutor.nexora.premises.models.PremisesId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface ConditionRepository : ReactiveCrudRepository<Condition, ConditionId> {
    fun findByConditionIdAndPremisesId(conditionId: ConditionId, premisesId: PremisesId): Mono<Condition>
}
