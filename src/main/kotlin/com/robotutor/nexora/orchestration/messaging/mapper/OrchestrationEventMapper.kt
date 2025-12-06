package com.robotutor.nexora.orchestration.messaging.mapper

import com.robotutor.nexora.orchestration.messaging.event.CompensateUserRegistrationEvent
import com.robotutor.nexora.orchestration.messaging.event.CompensateUserRegistrationEventMessage
import com.robotutor.nexora.orchestration.messaging.event.OrchestrationEvent
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

object OrchestrationEventMapper : EventMapper<OrchestrationEvent> {
    override fun toEventMessage(event: OrchestrationEvent): EventMessage {
        return when (event) {
            is CompensateUserRegistrationEvent -> toCompensateUserRegistrationEventMessage(event)
        }
    }

    private fun toCompensateUserRegistrationEventMessage(event: CompensateUserRegistrationEvent): CompensateUserRegistrationEventMessage {
        return CompensateUserRegistrationEventMessage(userId = event.userId)
    }
}