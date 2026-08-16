package com.robotutor.nexora.module.premises.domain.repository

import com.robotutor.nexora.module.premises.domain.aggregate.Premises
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface PremisesRepository {
    fun save(premises: Premises): Mono<Premises>
    fun findAllByPremisesIdIn(premisesIds: List<PremisesId>): Flux<Premises>
    fun findByPremisesId(premisesId: PremisesId): Mono<Premises>
    fun deleteByPremisesIdAndOwnerId(premisesId: PremisesId, ownerId: AccountId): Mono<Premises>
}
