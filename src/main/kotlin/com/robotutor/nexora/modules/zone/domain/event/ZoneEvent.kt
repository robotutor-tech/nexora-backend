package com.robotutor.nexora.modules.zone.domain.event

import com.robotutor.nexora.shared.domain.DomainEvent

sealed class ZoneEvent(name: String) : DomainEvent("zone.$name")
