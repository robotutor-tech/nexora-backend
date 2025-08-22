package com.robotutor.nexora.modules.premises.adapters.persistance.repository

import com.robotutor.nexora.modules.premises.adapters.persistance.model.PremisesDocument
import com.robotutor.nexora.modules.premises.domain.model.Premises
import com.robotutor.nexora.modules.premises.domain.repository.PremisesRepository
import com.robotutor.nexora.shared.domain.model.PremisesId
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class MongoPremisesRepository(private val premisesDocumentRepository: PremisesDocumentRepository) : PremisesRepository {
    override fun save(premises: Premises): Mono<Premises> {
        return premisesDocumentRepository.save(PremisesDocument.from(premises))
            .map { it.toDomainModel() }
    }

    override fun findAllByPremisesIdIn(premisesIds: List<PremisesId>): Flux<Premises> {
        return premisesDocumentRepository.findAllByPremisesIdIn(premisesIds.map { it.value })
            .map { it.toDomainModel() }
    }

    override fun findByPremisesId(premisesId: PremisesId): Mono<Premises> {
        return premisesDocumentRepository.findByPremisesId(premisesId.value)
            .map { it.toDomainModel() }
    }
}