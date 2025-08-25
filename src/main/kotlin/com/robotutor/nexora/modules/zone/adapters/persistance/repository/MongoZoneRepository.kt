package com.robotutor.nexora.modules.zone.adapters.persistance.repository

import com.robotutor.nexora.modules.zone.adapters.persistance.model.ZoneDocument
import com.robotutor.nexora.modules.zone.domain.model.Zone
import com.robotutor.nexora.modules.zone.domain.model.repository.ZoneRepository
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ZoneId
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class MongoZoneRepository(private val zoneDocumentRepository: ZoneDocumentRepository) : ZoneRepository {
    override fun save(zone: Zone): Mono<Zone> {
        return zoneDocumentRepository.save(ZoneDocument.from(zone))
            .map { it.toDomainModel() }
    }

    override fun findAllByPremisesIdAndZoneIdIn(premisesId: PremisesId, zoneIds: List<ZoneId>): Flux<Zone> {
      return zoneDocumentRepository.findAllByPremisesIdAndZoneIdIn(premisesId.value, zoneIds.map { it.value })
          .map {it.toDomainModel()}
    }
}