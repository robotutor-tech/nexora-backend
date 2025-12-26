package com.robotutor.nexora.context.zone.interfaces.controller.mapper

import com.robotutor.nexora.context.device.domain.vo.ModelNo
import com.robotutor.nexora.context.zone.application.command.CreateWidgetsCommand
import com.robotutor.nexora.context.zone.application.command.CreateZoneCommand
import com.robotutor.nexora.context.zone.application.command.GetZoneQuery
import com.robotutor.nexora.context.zone.application.command.GetZonesQuery
import com.robotutor.nexora.context.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.context.zone.domain.entity.Widget
import com.robotutor.nexora.context.zone.interfaces.controller.view.WidgetResponse
import com.robotutor.nexora.context.zone.interfaces.controller.view.WidgetsRequest
import com.robotutor.nexora.shared.domain.vo.ZoneId
import com.robotutor.nexora.context.zone.interfaces.controller.view.ZoneRequest
import com.robotutor.nexora.context.zone.interfaces.controller.view.ZoneResponse
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.FeedId
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
            widgets = zone.getWidgets().map { toWidgetResponse(it) },
            createdAt = zone.createdAt,
            updatedAt = zone.getUpdatedAt()
        )
    }

    fun getZonesQuery(resources: AuthorizedResources): GetZonesQuery {
        return GetZonesQuery(resources.toResources(ZoneId::class.java))
    }

    fun getZoneQuery(zoneId: String, actorData: ActorData): GetZoneQuery {
        return GetZoneQuery(premisesId = actorData.premisesId, zoneId = ZoneId(zoneId))
    }

    fun toCreateWidgetsCommand(request: WidgetsRequest, actorData: ActorData): CreateWidgetsCommand {
        return CreateWidgetsCommand(
            zoneId = ZoneId(request.zoneId),
            modelNo = ModelNo(request.modelNo),
            feedIds = request.feedIds.map { FeedId(it) },
            premisesId = actorData.premisesId,
        )
    }

    private fun toWidgetResponse(widget: Widget): WidgetResponse {
        return WidgetResponse(
            feedId = widget.feedId.value,
            widgetId = widget.widgetId.value
        )
    }
}