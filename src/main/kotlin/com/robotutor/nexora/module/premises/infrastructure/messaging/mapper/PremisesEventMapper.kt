package com.robotutor.nexora.module.premises.infrastructure.messaging.mapper

import com.robotutor.nexora.module.premises.domain.event.PremisesEvent
import com.robotutor.nexora.module.premises.domain.event.PremisesRegisteredEvent
import com.robotutor.nexora.module.premises.infrastructure.messaging.message.PremisesRegisteredEventMessage
import com.robotutor.nexora.common.messaging.mapper.EventMapper
import com.robotutor.nexora.common.messaging.message.EventMessage

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