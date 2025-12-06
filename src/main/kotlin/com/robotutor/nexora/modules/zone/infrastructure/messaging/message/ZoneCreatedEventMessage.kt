package com.robotutor.nexora.modules.zone.infrastructure.messaging.message

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

data class ZoneCreatedEventMessage(val zoneId: String) : EventMessage("zone.created")