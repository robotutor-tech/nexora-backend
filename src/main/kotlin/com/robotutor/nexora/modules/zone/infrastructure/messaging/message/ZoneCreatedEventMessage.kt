package com.robotutor.nexora.modules.zone.infrastructure.messaging.message

import com.robotutor.nexora.shared.domain.event.EventMessage

data class ZoneCreatedEventMessage(val zoneId: String) : EventMessage