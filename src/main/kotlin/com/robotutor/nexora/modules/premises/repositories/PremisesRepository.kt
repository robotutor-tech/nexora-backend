package com.robotutor.nexora.modules.premises.repositories

import com.robotutor.nexora.modules.premises.models.Premises
import com.robotutor.nexora.modules.premises.models.PremisesId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface PremisesRepository : ReactiveCrudRepository<Premises, PremisesId> {
    fun findAllByPremisesIdIn(premisesIds: List<PremisesId>): Flux<Premises>
    fun findByPremisesId(premisesId: PremisesId): Mono<Premises>
    fun deleteByPremisesId(premisesId: PremisesId): Mono<Premises>
}
