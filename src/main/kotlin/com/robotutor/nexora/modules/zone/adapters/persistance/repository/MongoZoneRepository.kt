package com.robotutor.nexora.modules.zone.adapters.persistance.repository

import com.robotutor.nexora.modules.zone.domain.model.Zone
import com.robotutor.nexora.modules.zone.domain.model.repository.ZoneRepository
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ZoneId
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class MongoZoneRepository: ZoneRepository {
    override fun save(zone: Zone): Mono<Zone> {
        TODO("Not yet implemented")
    }

    override fun findAllByPremisesIdAndZoneIdIn(
        premisesId: PremisesId,
        zoneIds: List<ZoneId>
    ): Flux<Zone> {
        TODO("Not yet implemented")
    }

    override fun findByZoneIdAndPremisesId(
        zoneId: ZoneId,
        premisesId: PremisesId
    ): Mono<Zone> {
        TODO("Not yet implemented")
    }
}