package com.robotutor.nexora.context.zone.interfaces.controller.mapper

import com.robotutor.nexora.context.zone.application.command.CreateZoneCommand
import com.robotutor.nexora.context.zone.application.command.GetZoneQuery
import com.robotutor.nexora.context.zone.application.command.GetZonesQuery
import com.robotutor.nexora.context.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.shared.domain.vo.ZoneId
import com.robotutor.nexora.context.zone.interfaces.controller.dto.ZoneRequest
import com.robotutor.nexora.context.zone.interfaces.controller.dto.ZoneResponse
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.interfaces.view.AuthorizedResources

object ZoneMapper {
    fun toCreateZoneCommand(request: ZoneRequest, actorData: ActorData): CreateZoneCommand {
        return CreateZoneCommand(name = Name(request.name), actorData.premisesId, actorData.actorId)
    }

    fun toZoneResponse(zone: ZoneAggregate): ZoneResponse {
        return ZoneResponse(
            zoneId = zone.zoneId.value,
            premisesId = zone.premisesId.value,
            name = zone.name.value,
            createdAt = zone.createdAt
        )
    }

    fun getZonesQuery(resources: AuthorizedResources): GetZonesQuery {
        return GetZonesQuery(resources.toResources(ZoneId::class.java))
    }

    fun getZoneQuery(zoneId: String, actorData: ActorData): GetZoneQuery {
        return GetZoneQuery(premisesId = actorData.premisesId, zoneId = ZoneId(zoneId))
    }
}