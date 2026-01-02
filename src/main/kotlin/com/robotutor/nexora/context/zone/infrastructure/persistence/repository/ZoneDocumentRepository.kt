package com.robotutor.nexora.context.zone.infrastructure.persistence.repository

import com.robotutor.nexora.context.zone.infrastructure.persistence.document.ZoneDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface ZoneDocumentRepository : ReactiveCrudRepository<ZoneDocument, String> {
    fun findByPremisesIdAndName(premisesId: String, name: String): Mono<ZoneDocument>
    fun findByPremisesIdAndZoneId(premisesId: String, zoneId: String): Mono<ZoneDocument>
    fun existsByPremisesIdAndName(premisesId: String, name: String): Mono<Boolean>
}