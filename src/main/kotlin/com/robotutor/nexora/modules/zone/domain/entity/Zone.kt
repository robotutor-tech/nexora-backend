package com.robotutor.nexora.modules.zone.domain.entity

import com.robotutor.nexora.modules.zone.domain.event.ZoneCreatedEvent
import com.robotutor.nexora.modules.zone.domain.event.ZoneEvent
import com.robotutor.nexora.shared.domain.event.DomainAggregate
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ZoneId
import java.time.Instant

data class Zone(
    val zoneId: ZoneId,
    val premisesId: PremisesId,
    val name: Name,
    val createdBy: ActorId,
    val createdAt: Instant = Instant.now(),
    val version: Long? = null
) : DomainAggregate<ZoneEvent>() {
    companion object {
        fun create(zoneId: ZoneId, premisesId: PremisesId, name: Name, createdBy: ActorId): Zone {
            val zone = Zone(zoneId = zoneId, premisesId = premisesId, name = name, createdBy = createdBy)
            zone.addDomainEvent(ZoneCreatedEvent(zone.zoneId, zone.name))
            return zone
        }
    }
}