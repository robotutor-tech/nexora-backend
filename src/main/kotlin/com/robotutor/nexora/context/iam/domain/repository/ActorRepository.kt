package com.robotutor.nexora.context.iam.domain.repository

import com.robotutor.nexora.context.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.shared.domain.specification.Specification
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ActorRepository {
    fun save(actorAggregate: ActorAggregate): Mono<ActorAggregate>
    fun findAllByAccountId(accountId: AccountId): Flux<ActorAggregate>
    fun findByAccountIdAndPremisesId(accountId: AccountId, premisesId: PremisesId): Mono<ActorAggregate>
    fun findByActorIdAndPremisesId(actorId: ActorId, premisesId: PremisesId): Mono<ActorAggregate>
    fun findByActorId(actorId: ActorId): Mono<ActorAggregate>
    fun findBySpecification(specification: Specification<ActorAggregate>): Mono<ActorAggregate>
}