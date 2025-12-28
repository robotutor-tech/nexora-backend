package com.robotutor.nexora.context.zone.infrastructure.messaging.message

import com.robotutor.nexora.common.messaging.infrastructure.message.EventMessage

data class ZoneCreatedEventMessage(val zoneId: String, val name: String) : EventMessage("zone.created")