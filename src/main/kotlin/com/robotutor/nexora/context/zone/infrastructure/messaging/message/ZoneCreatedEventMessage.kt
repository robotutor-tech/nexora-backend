package com.robotutor.nexora.context.zone.infrastructure.messaging.message

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

data class ZoneCreatedEventMessage(val zoneId: String) : EventMessage("zone.created")