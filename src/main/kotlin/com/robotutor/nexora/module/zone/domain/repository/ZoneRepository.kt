package com.robotutor.nexora.module.zone.domain.repository

import com.robotutor.nexora.module.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.shared.domain.specification.Specification
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ZoneId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ZoneRepository {
    fun save(zone: ZoneAggregate): Mono<ZoneAggregate>
    fun findByPremisesIdAndName(premisesId: PremisesId, name: Name): Mono<ZoneAggregate>
    fun findByZoneIdAndPremisesId(zoneId: ZoneId, premisesId: PremisesId): Mono<ZoneAggregate>
    fun findAll(specification: Specification<ZoneAggregate>): Flux<ZoneAggregate>
    fun existsByPremisesIdAndName(premisesId: PremisesId, name: Name): Mono<Boolean>
}