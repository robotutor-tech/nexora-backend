package com.robotutor.nexora.modules.zone.adapters.persistance.repository

import com.robotutor.nexora.modules.zone.adapters.persistance.model.ZoneDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ZoneDocumentRepository : ReactiveCrudRepository<ZoneDocument, String> {
    fun findAllByPremisesIdAndZoneIdIn(premisesId: String, zoneIds: List<String>): Flux<ZoneDocument>
}