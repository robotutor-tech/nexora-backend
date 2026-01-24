package com.robotutor.nexora.module.automation.infrastructure.messaging.mapper

import com.robotutor.nexora.common.message.mapper.EventMapper
import com.robotutor.nexora.common.message.message.EventMessage
import com.robotutor.nexora.module.automation.domain.event.AutomationEvent
import com.robotutor.nexora.module.automation.domain.event.AutomationRegisteredEvent
import com.robotutor.nexora.module.automation.infrastructure.messaging.message.AutomationRegisteredEventMessage

object AutomationEventMapper : EventMapper<AutomationEvent> {
    override fun toEventMessage(event: AutomationEvent): EventMessage {
        return when (event) {
            is AutomationRegisteredEvent -> AutomationRegisteredEventMessage(event.automationId.value)
        }
    }
}