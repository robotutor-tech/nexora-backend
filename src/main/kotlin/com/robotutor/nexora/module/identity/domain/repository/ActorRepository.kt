package com.robotutor.nexora.module.identity.domain.repository

import com.robotutor.nexora.module.identity.domain.aggregate.Actor
import com.robotutor.nexora.shared.domain.specification.Specification
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ActorRepository {
    fun save(actor: Actor): Mono<Actor>
    fun findAllByAccountId(accountId: AccountId): Flux<Actor>
    fun findByAccountIdAndPremisesId(accountId: AccountId, premisesId: PremisesId): Mono<Actor>
    fun findByActorIdAndPremisesId(actorId: ActorId, premisesId: PremisesId): Mono<Actor>
    fun findByActorId(actorId: ActorId): Mono<Actor>
    fun findBySpecification(specification: Specification<Actor>): Mono<Actor>
    fun exitsBySpecification(specification: Specification<Actor>): Mono<Boolean>
}
