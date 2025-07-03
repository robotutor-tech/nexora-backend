package com.robotutor.nexora.automation.repositories

import com.robotutor.nexora.automation.models.Action
import com.robotutor.nexora.automation.models.ActionId
import com.robotutor.nexora.premises.models.PremisesId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ActionRepository : ReactiveCrudRepository<Action, ActionId> {
    fun findAllByActionIdInAndPremisesId(actionIds: List<ActionId>, premisesId: PremisesId): Flux<Action>
}
