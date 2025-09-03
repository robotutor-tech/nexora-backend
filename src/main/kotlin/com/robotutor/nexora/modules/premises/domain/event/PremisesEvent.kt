package com.robotutor.nexora.modules.premises.domain.event

import com.robotutor.nexora.shared.domain.event.DomainEvent

sealed class PremisesEvent(name: String) : DomainEvent("premises.$name")