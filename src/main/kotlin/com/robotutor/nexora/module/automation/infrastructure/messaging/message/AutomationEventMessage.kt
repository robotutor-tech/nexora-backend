package com.robotutor.nexora.module.automation.infrastructure.messaging.message

import com.robotutor.nexora.common.message.message.EventMessage

sealed class AutomationEventMessage(name: String) : EventMessage(eventName = "automation.$name")

data class AutomationRegisteredEventMessage(val automationId: String) : AutomationEventMessage("registered")