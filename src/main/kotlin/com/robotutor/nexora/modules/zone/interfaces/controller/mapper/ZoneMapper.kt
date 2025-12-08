package com.robotutor.nexora.modules.zone.interfaces.controller.mapper

import com.robotutor.nexora.modules.zone.application.command.CreateZoneCommand
import com.robotutor.nexora.modules.zone.domain.entity.Zone
import com.robotutor.nexora.modules.zone.interfaces.controller.dto.ZoneRequest
import com.robotutor.nexora.modules.zone.interfaces.controller.dto.ZoneResponse
import com.robotutor.nexora.shared.domain.vo.Name

object ZoneMapper {
    fun toCreateZoneCommand(request: ZoneRequest): CreateZoneCommand {
        return CreateZoneCommand(name = Name(request.name))
    }

    fun toZoneResponse(zone: Zone): ZoneResponse {
        return ZoneResponse(
            zoneId = zone.zoneId.value,
            premisesId = zone.premisesId.value,
            name = zone.name.value,
            createdAt = zone.createdAt
        )
    }

}