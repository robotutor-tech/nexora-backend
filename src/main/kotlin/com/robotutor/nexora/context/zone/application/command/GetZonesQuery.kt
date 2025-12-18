package com.robotutor.nexora.context.zone.application.command

import com.robotutor.nexora.shared.domain.vo.ZoneId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.Resources

data class GetZonesQuery(
    val resources: Resources<ZoneId>
)

data class GetZoneQuery(
    val premisesId: PremisesId,
    val zoneId: ZoneId
)
