package com.robotutor.nexora.module.zone.infrastructure.messaging.mapper

import com.robotutor.nexora.common.message.mapper.EventMapper
import com.robotutor.nexora.common.message.message.EventMessage
import com.robotutor.nexora.module.zone.domain.event.ZoneCreatedEvent
import com.robotutor.nexora.module.zone.domain.event.ZoneEvent
import com.robotutor.nexora.module.zone.infrastructure.messaging.message.ZoneCreatedEventMessage

object ZoneEventMapper : EventMapper<ZoneEvent> {
    override fun toEventMessage(event: ZoneEvent): EventMessage {
        return when (event) {
            is ZoneCreatedEvent -> toZoneCreatedEventMessage(event)
        }
    }

    private fun toZoneCreatedEventMessage(event: ZoneCreatedEvent): ZoneCreatedEventMessage {
        return ZoneCreatedEventMessage(event.zoneId.value, event.name.value)
    }
}