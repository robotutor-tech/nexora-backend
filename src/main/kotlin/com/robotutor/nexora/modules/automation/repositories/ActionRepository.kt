package com.robotutor.nexora.modules.automation.repositories

import com.robotutor.nexora.modules.automation.models.ActionId
import com.robotutor.nexora.modules.automation.models.documents.ActionDocument
import com.robotutor.nexora.modules.premises.models.PremisesId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface ActionRepository : ReactiveCrudRepository<ActionDocument, ActionId> {
    fun findAllByActionIdInAndPremisesId(actions: List<ActionId>, premisesId: PremisesId): Flux<ActionDocument>
    fun findByPremisesIdAndConfig(premisesId: PremisesId, config: Map<String, Any?>): Mono<ActionDocument>

    fun findByActionIdAndPremisesId(actionsId: ActionId, premisesId: PremisesId): Mono<ActionDocument>
}
