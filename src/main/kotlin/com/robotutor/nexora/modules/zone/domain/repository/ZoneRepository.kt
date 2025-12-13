package com.robotutor.nexora.modules.zone.domain.repository

import com.robotutor.nexora.modules.zone.domain.entity.Zone
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.model.ZoneId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ZoneRepository {
    fun save(zone: Zone): Mono<Zone>
    fun findAllByPremisesIdAndZoneIdIn(premisesId: PremisesId, zoneIds: List<ZoneId>): Flux<Zone>
//    fun findByZoneIdAndPremisesId(zoneId: ZoneId, premisesId: PremisesId): Mono<Zone>
}