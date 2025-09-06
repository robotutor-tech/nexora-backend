package com.robotutor.nexora.modules.automation.domain.repository

import com.robotutor.nexora.modules.automation.domain.entity.Action
import com.robotutor.nexora.modules.automation.domain.entity.ActionId
import com.robotutor.nexora.modules.automation.domain.entity.config.ActionConfig
import com.robotutor.nexora.shared.domain.model.PremisesId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ActionRepository {
    fun findByPremisesIdAndConfig(premisesId: PremisesId, config: ActionConfig): Mono<Action>
    fun save(action: Action): Mono<Action>
    fun findAllByPremisesIdAndActionIdIn(premisesId: PremisesId, actionsIds: List<ActionId>): Flux<Action>
    fun findByActionIdAndPremisesId(actionId: ActionId, premisesId: PremisesId): Mono<Action>
}