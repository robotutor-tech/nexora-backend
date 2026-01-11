package com.robotutor.nexora.module.automation.domain.event

import com.robotutor.nexora.module.automation.domain.vo.AutomationId
import com.robotutor.nexora.shared.domain.Event

sealed interface AutomationEvent : Event

data class AutomationRegisteredEvent(val automationId: AutomationId) : AutomationEvent