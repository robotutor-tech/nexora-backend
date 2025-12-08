package com.robotutor.nexora.modules.zone.domain.event

import com.robotutor.nexora.shared.domain.model.ZoneId
import com.robotutor.nexora.shared.domain.vo.Name

data class ZoneCreatedEvent(
    val zoneId: ZoneId,
    val name: Name
) : ZoneEvent
