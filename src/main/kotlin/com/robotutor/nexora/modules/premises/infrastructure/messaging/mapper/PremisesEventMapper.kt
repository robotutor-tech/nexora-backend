package com.robotutor.nexora.modules.premises.infrastructure.messaging.mapper

import com.robotutor.nexora.modules.premises.domain.event.PremisesCreatedEvent
import com.robotutor.nexora.modules.premises.domain.event.PremisesEvent
import com.robotutor.nexora.modules.premises.infrastructure.messaging.message.PremisesCreatedEventMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.domain.event.EventMessage
import org.springframework.stereotype.Service

@Service
class PremisesEventMapper : EventMapper<PremisesEvent> {
    override fun toEventMessage(event: PremisesEvent): EventMessage {
        return when (event) {
            is PremisesCreatedEvent -> toPremisesCreatedEventMessage(event)
        }
    }

    private fun toPremisesCreatedEventMessage(event: PremisesCreatedEvent): PremisesCreatedEventMessage {
        return PremisesCreatedEventMessage(event.premisesId.value, event.name.value)
    }
}