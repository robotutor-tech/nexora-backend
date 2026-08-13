package com.robotutor.nexora.module.zone.infrastructure.messaging.message

import com.robotutor.nexora.shared.message.message.EventMessage

data class ZoneCreatedEventMessage(val zoneId: String, val name: String) : EventMessage("zone.created")
