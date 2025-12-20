package com.robotutor.nexora.context.premises.domain.repository

import com.robotutor.nexora.context.premises.domain.aggregate.PremisesAggregate
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface PremisesRepository {
    fun save(premisesAggregate: PremisesAggregate): Mono<PremisesAggregate>
    fun findAllByPremisesIdIn(premisesIds: List<PremisesId>): Flux<PremisesAggregate>
    fun findByPremisesId(premisesId: PremisesId): Mono<PremisesAggregate>
    fun deleteByPremisesIdAndOwnerId(premisesId: PremisesId, ownerId: AccountId): Mono<PremisesAggregate>
}