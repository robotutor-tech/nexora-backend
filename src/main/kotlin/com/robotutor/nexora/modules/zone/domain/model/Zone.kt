package com.robotutor.nexora.modules.zone.domain.model

import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ZoneId
import java.time.Instant

data class Zone(
    val zoneId: ZoneId,
    val premisesId: PremisesId,
    val name: String,
    val createdBy: ActorId,
    val createdAt: Instant = Instant.now(),
    val version: Long? = null
)