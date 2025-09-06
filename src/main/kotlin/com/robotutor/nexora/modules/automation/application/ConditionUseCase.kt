package com.robotutor.nexora.modules.automation.application

import com.robotutor.nexora.modules.automation.domain.entity.Condition
import com.robotutor.nexora.modules.automation.domain.entity.ConditionId
import com.robotutor.nexora.shared.domain.model.ActorData
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ConditionUseCase {
    fun createCondition(createConditionCommand: Any, actorData: ActorData): Mono<Condition> {
        TODO("Not yet implemented")
    }

    fun getConditions(conditionIds: List<ConditionId>, actorData: ActorData): Flux<Condition> {
        TODO("Not yet implemented")
    }

}
