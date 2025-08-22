package com.robotutor.nexora.modules.premises.domain.repository

import com.robotutor.nexora.modules.premises.domain.model.Premises
import com.robotutor.nexora.shared.domain.model.PremisesId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface PremisesRepository {
    fun save(premises: Premises): Mono<Premises>
    fun findAllByPremisesIdIn(premisesIds: List<PremisesId>): Flux<Premises>
    fun findByPremisesId(premisesId: PremisesId): Mono<Premises>
}