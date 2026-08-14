package com.robotutor.nexora.module.automation.infrastructure.messaging.message

import com.robotutor.nexora.shared.message.config.EventName
import com.robotutor.nexora.shared.message.message.EventMessage

sealed interface AutomationEventMessage : EventMessage

data class AutomationRegisteredEventMessage(val automationId: String) : AutomationEventMessage {
    override val eventName: EventName = EventName.AUTOMATION_REGISTERED
}
