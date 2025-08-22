package com.robotutor.nexora.modules.premises.adapters.persistance.repository

import com.robotutor.nexora.modules.premises.adapters.persistance.model.PremisesDocument
import com.robotutor.nexora.modules.premises.domain.model.Premises
import com.robotutor.nexora.shared.domain.model.PremisesId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface PremisesDocumentRepository : ReactiveCrudRepository<PremisesDocument, String> {
    fun findAllByPremisesIdIn(premisesIds: List<String>): Flux<PremisesDocument>
    fun findByPremisesId(premisesId: String): Mono<PremisesDocument>
}