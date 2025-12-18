package com.robotutor.nexora.context.zone.domain.aggregate

import com.robotutor.nexora.context.zone.domain.event.ZoneCreatedEvent
import com.robotutor.nexora.context.zone.domain.event.ZoneDomainEvent
import com.robotutor.nexora.shared.domain.vo.ZoneId
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import java.time.Instant

class ZoneAggregate private constructor(
    val zoneId: ZoneId,
    val premisesId: PremisesId,
    val name: Name,
    val createdBy: ActorId,
    val createdAt: Instant,
    val updatedAt: Instant,
) : AggregateRoot<ZoneAggregate, ZoneId, ZoneDomainEvent>(zoneId) {
    companion object {
        fun create(
            zoneId: ZoneId,
            premisesId: PremisesId,
            name: Name,
            createdBy: ActorId,
            createdAt: Instant = Instant.now(),
            updatedAt: Instant = Instant.now(),
        ): ZoneAggregate {
            return ZoneAggregate(zoneId, premisesId, name, createdBy, createdAt, updatedAt)
        }

        fun createZone(premisesId: PremisesId, name: Name, createdBy: ActorId): ZoneAggregate {
            val zone = create(ZoneId.generate(), premisesId, name, createdBy)
            zone.addEvent(ZoneCreatedEvent(zone.zoneId, zone.name, zone.premisesId, zone.createdBy))
            return zone
        }
    }
}