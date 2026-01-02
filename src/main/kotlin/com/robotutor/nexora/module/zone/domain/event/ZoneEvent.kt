package com.robotutor.nexora.module.zone.domain.event

import com.robotutor.nexora.shared.domain.vo.ZoneId
import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId

sealed interface ZoneEvent : Event

data class ZoneCreatedEvent(val zoneId: ZoneId, val name: Name, val premisesId: PremisesId, val createdBy: ActorId) :
    ZoneEvent
