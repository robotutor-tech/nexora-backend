package com.robotutor.nexora.module.automation.infrastructure.messaging.mapper

import com.robotutor.nexora.shared.message.mapper.EventMapper
import com.robotutor.nexora.shared.message.message.EventMessage
import com.robotutor.nexora.module.automation.domain.event.AutomationEvent
import com.robotutor.nexora.module.automation.domain.event.AutomationRegisteredEvent
import com.robotutor.nexora.module.automation.infrastructure.messaging.message.AutomationRegisteredEventMessage
import reactor.core.publisher.Mono

object AutomationEventMapper : EventMapper<AutomationEvent> {
    override fun toEventMessage(event: AutomationEvent): EventMessage {
        return when (event) {
            is AutomationRegisteredEvent -> AutomationRegisteredEventMessage(event.automationId.value)
        }
    }
}
