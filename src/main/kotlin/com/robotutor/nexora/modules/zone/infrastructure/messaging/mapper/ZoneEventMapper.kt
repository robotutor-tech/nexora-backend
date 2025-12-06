package com.robotutor.nexora.modules.zone.infrastructure.messaging.mapper

import com.robotutor.nexora.modules.zone.domain.event.ZoneCreatedEvent
import com.robotutor.nexora.modules.zone.domain.event.ZoneEvent
import com.robotutor.nexora.modules.zone.infrastructure.messaging.message.ZoneCreatedEventMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

object ZoneEventMapper : EventMapper<ZoneEvent> {
    override fun toEventMessage(event: ZoneEvent): EventMessage {
        return when (event) {
            is ZoneCreatedEvent -> toZoneCreatedEventMessage(event)
        }
    }

    private fun toZoneCreatedEventMessage(event: ZoneCreatedEvent): ZoneCreatedEventMessage {
        return ZoneCreatedEventMessage(event.zoneId.value)
    }
}