package com.robotutor.nexora.modules.automation.domain.event

import com.robotutor.nexora.shared.domain.event.DomainEvent

sealed class AutomationEvent(name: String) : DomainEvent("automation.$name")