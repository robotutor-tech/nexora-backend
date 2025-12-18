package com.robotutor.nexora.context.zone.infrastructure.messaging.mapper

import com.robotutor.nexora.context.zone.domain.event.ZoneCreatedEvent
import com.robotutor.nexora.context.zone.domain.event.ZoneDomainEvent
import com.robotutor.nexora.context.zone.infrastructure.messaging.message.ZoneCreatedEventMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

object ZoneDomainEventMapper : EventMapper<ZoneDomainEvent> {
    override fun toEventMessage(event: ZoneDomainEvent): EventMessage {
        return when (event) {
            is ZoneCreatedEvent -> toZoneCreatedEventMessage(event)
        }
    }

    private fun toZoneCreatedEventMessage(event: ZoneCreatedEvent): ZoneCreatedEventMessage {
        return ZoneCreatedEventMessage(event.zoneId.value, event.name.value)
    }
}