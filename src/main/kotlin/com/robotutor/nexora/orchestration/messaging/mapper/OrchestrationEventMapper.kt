package com.robotutor.nexora.orchestration.messaging.mapper

import com.robotutor.nexora.orchestration.messaging.event.CompensateAccountRegistrationEvent
import com.robotutor.nexora.orchestration.messaging.event.CompensateAccountRegistrationEventMessage
import com.robotutor.nexora.orchestration.messaging.event.CompensatePremisesRegistrationEvent
import com.robotutor.nexora.orchestration.messaging.event.CompensatePremisesRegistrationEventMessage
import com.robotutor.nexora.orchestration.messaging.event.OrchestrationEvent
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

object OrchestrationEventMapper : EventMapper<OrchestrationEvent> {
    override fun toEventMessage(event: OrchestrationEvent): EventMessage {
        return when (event) {
            is CompensateAccountRegistrationEvent -> toCompensateAccountRegistrationEventMessage(event)
            is CompensatePremisesRegistrationEvent -> toCompensatePremisesRegistrationEventMessage(event)
        }
    }

    private fun toCompensateAccountRegistrationEventMessage(event: CompensateAccountRegistrationEvent): CompensateAccountRegistrationEventMessage {
        return CompensateAccountRegistrationEventMessage(event.accountId)
    }

    private fun toCompensatePremisesRegistrationEventMessage(event: CompensatePremisesRegistrationEvent): CompensatePremisesRegistrationEventMessage {
        return CompensatePremisesRegistrationEventMessage(event.premisesId)
    }
}