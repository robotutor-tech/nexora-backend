package com.robotutor.nexora.modules.zone.domain.event

import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.ZoneId

data class ZoneCreatedEvent(
    val zoneId: ZoneId,
    val name: Name
) : ZoneEvent
