package com.robotutor.nexora.modules.zone.repositories

import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.modules.zone.models.Zone
import com.robotutor.nexora.modules.zone.models.ZoneId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface ZoneRepository : ReactiveCrudRepository<Zone, ZoneId> {
    fun findAllByPremisesIdAndZoneIdIn(premisesId: PremisesId, zoneId: List<ZoneId>): Flux<Zone>
    fun findByZoneIdAndPremisesId(zoneId: ZoneId, premisesId: PremisesId): Mono<Zone>
}
