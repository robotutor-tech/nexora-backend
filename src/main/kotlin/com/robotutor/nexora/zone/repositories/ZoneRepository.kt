package com.robotutor.nexora.zone.repositories

import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.zone.models.Zone
import com.robotutor.nexora.zone.models.ZoneId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface ZoneRepository : ReactiveCrudRepository<Zone, ZoneId> {
    fun findAllByPremisesId(premisesId: PremisesId): Flux<Zone>
    fun findByZoneIdAndPremisesId(zoneId: ZoneId, premisesId: PremisesId): Mono<Zone>
}
