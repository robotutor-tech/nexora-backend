package com.robotutor.nexora.modules.automation.domain.repository

import com.robotutor.nexora.modules.automation.domain.entity.Trigger
import com.robotutor.nexora.modules.automation.domain.entity.TriggerId
import com.robotutor.nexora.modules.automation.domain.entity.config.TriggerConfig
import com.robotutor.nexora.shared.domain.model.PremisesId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TriggerRepository {
    fun findByPremisesIdAndConfig(premisesId: PremisesId, config: TriggerConfig): Mono<Trigger>
    fun save(trigger: Trigger): Mono<Trigger>
    fun findAllByPremisesIdAndTriggerIdIn(premisesId: PremisesId, triggersIds: List<TriggerId>): Flux<Trigger>
    fun findByTriggerIdAndPremisesId(triggerId: TriggerId, premisesId: PremisesId): Mono<Trigger>
}