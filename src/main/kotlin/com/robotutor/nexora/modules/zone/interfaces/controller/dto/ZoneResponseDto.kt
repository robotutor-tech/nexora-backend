package com.robotutor.nexora.modules.zone.interfaces.controller.dto

import com.robotutor.nexora.modules.zone.domain.model.Zone
import java.time.Instant

data class ZoneView(
    val zoneId: String,
    val premisesId: String,
    val name: String,
    val createdAt: Instant
) {
    companion object {
        fun from(zone: Zone): ZoneView {
            return ZoneView(
                premisesId = zone.premisesId.value,
                name = zone.name,
                createdAt = zone.createdAt,
                zoneId = zone.zoneId.value
            )
        }
    }
}
