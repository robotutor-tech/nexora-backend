package com.robotutor.nexora.module.premises.infrastructure.persistence.repository

import com.robotutor.nexora.module.premises.infrastructure.persistence.document.PremisesDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface PremisesDocumentRepository : ReactiveCrudRepository<PremisesDocument, String> {
    fun findAllByPremisesIdIn(premisesIds: List<String>): Flux<PremisesDocument>
    fun findByPremisesId(premisesId: String): Mono<PremisesDocument>
    fun deleteByPremisesIdAndOwnerId(premisesId: String, ownerId: String): Mono<PremisesDocument>
}