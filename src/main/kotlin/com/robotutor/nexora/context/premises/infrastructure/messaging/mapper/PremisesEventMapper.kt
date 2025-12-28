package com.robotutor.nexora.context.premises.infrastructure.messaging.mapper

import com.robotutor.nexora.context.premises.domain.event.PremisesEvent
import com.robotutor.nexora.context.premises.domain.event.PremisesRegisteredEvent
import com.robotutor.nexora.context.premises.infrastructure.messaging.message.PremisesRegisteredEventMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.common.messaging.infrastructure.message.EventMessage

object PremisesEventMapper : EventMapper<PremisesEvent> {
    override fun toEventMessage(event: PremisesEvent): EventMessage {
        return when (event) {
            is PremisesRegisteredEvent -> toPremisesRegisteredEventMessage(event)
        }
    }

    private fun toPremisesRegisteredEventMessage(event: PremisesRegisteredEvent): PremisesRegisteredEventMessage {
        return PremisesRegisteredEventMessage(premisesId = event.premisesId.value, name = event.name.value)
    }
}