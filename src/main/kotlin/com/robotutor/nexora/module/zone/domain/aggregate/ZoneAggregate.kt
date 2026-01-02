package com.robotutor.nexora.module.zone.domain.aggregate

import com.robotutor.nexora.module.zone.domain.entity.Widget
import com.robotutor.nexora.module.zone.domain.event.ZoneCreatedEvent
import com.robotutor.nexora.module.zone.domain.event.ZoneEvent
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
    private var widgets: MutableList<Widget>,
    private var updatedAt: Instant,
) : AggregateRoot<ZoneAggregate, ZoneId, ZoneEvent>(zoneId) {

    fun getWidgets(): List<Widget> = widgets.toList()
    fun getUpdatedAt(): Instant = updatedAt

    companion object {
        fun create(
            zoneId: ZoneId,
            premisesId: PremisesId,
            name: Name,
            createdBy: ActorId,
            widgets: List<Widget> = emptyList(),
            createdAt: Instant = Instant.now(),
            updatedAt: Instant = Instant.now(),
        ): ZoneAggregate {
            return ZoneAggregate(
                zoneId = zoneId,
                premisesId = premisesId,
                name = name,
                createdBy = createdBy,
                widgets = widgets.toMutableList(),
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
        }

        fun createZone(premisesId: PremisesId, name: Name, createdBy: ActorId): ZoneAggregate {
            val zone = create(ZoneId.generate(), premisesId, name, createdBy)
            zone.addEvent(ZoneCreatedEvent(zone.zoneId, zone.name, zone.premisesId, zone.createdBy))
            return zone
        }
    }

    fun updateWidgets(widgets: List<Widget>): ZoneAggregate {
        this.widgets.addAll(widgets)
        this.updatedAt = Instant.now()
        return this
    }
}