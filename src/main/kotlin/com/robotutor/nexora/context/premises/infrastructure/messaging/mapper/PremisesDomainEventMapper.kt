package com.robotutor.nexora.context.premises.infrastructure.messaging.mapper

import com.robotutor.nexora.context.premises.domain.event.PremisesDomainEvent
import com.robotutor.nexora.context.premises.domain.event.PremisesRegisteredEvent
import com.robotutor.nexora.context.premises.infrastructure.messaging.message.PremisesRegisteredEventMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

object PremisesDomainEventMapper : EventMapper<PremisesDomainEvent> {
    override fun toEventMessage(event: PremisesDomainEvent): EventMessage {
        return when (event) {
            is PremisesRegisteredEvent -> toPremisesRegisteredEventMessage(event)
        }
    }

    private fun toPremisesRegisteredEventMessage(event: PremisesRegisteredEvent): PremisesRegisteredEventMessage {
        return PremisesRegisteredEventMessage(premisesId = event.premisesId.value, name = event.name.value)
    }
}