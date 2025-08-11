package com.robotutor.nexora.modules.zone.controllers.view

import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.modules.zone.models.Zone
import com.robotutor.nexora.modules.zone.models.ZoneId
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant

data class ZoneCreateRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 30, message = "Name should not be less than 2 char or more than 30 char")
    val name: String
)

data class ZoneView(
    val zoneId: ZoneId,
    val premisesId: PremisesId,
    val name: String,
    val createdAt: Instant
) {
    companion object {
        fun from(zone: Zone): ZoneView {
            return ZoneView(
                premisesId = zone.premisesId,
                name = zone.name,
                createdAt = zone.createdAt,
                zoneId = zone.zoneId
            )
        }
    }
}
