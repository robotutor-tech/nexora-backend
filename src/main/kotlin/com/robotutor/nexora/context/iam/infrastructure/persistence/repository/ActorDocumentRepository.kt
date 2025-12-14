package com.robotutor.nexora.context.iam.infrastructure.persistence.repository

import com.robotutor.nexora.context.iam.infrastructure.persistence.document.ActorDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface ActorDocumentRepository : ReactiveCrudRepository<ActorDocument, String> {
    fun findAllByAccountId(accountId: String): Flux<ActorDocument>
    fun findByAccountIdAndPremisesId(accountId: String, premisesId: String): Mono<ActorDocument>
    fun findByActorIdAndPremisesId(actorId: String, premisesId: String): Mono<ActorDocument>
    fun findByActorId(actorId: String): Mono<ActorDocument>
}