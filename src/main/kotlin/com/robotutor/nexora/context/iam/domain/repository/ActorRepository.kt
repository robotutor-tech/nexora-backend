package com.robotutor.nexora.context.iam.domain.repository

import com.robotutor.nexora.context.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.shared.domain.vo.AccountId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ActorRepository {
    fun save(actorAggregate: ActorAggregate): Mono<ActorAggregate>
    fun findAllByAccountId(accountId: AccountId): Flux<ActorAggregate>
}