package com.robotutor.nexora.modules.zone.interfaces.controller.mapper

import com.robotutor.nexora.common.security.models.PremisesActorData
import com.robotutor.nexora.modules.zone.application.command.CreateZoneCommand
import com.robotutor.nexora.modules.zone.interfaces.controller.dto.ZoneCreateRequest

class ZoneMapper {
    companion object {
        fun toCreateZoneCommand(request: ZoneCreateRequest, premisesActorData: PremisesActorData): CreateZoneCommand {
            return CreateZoneCommand(
                premisesId = premisesActorData.premisesId,
                name = request.name,
                createdBy = premisesActorData.actorId,
            )
        }
    }

}