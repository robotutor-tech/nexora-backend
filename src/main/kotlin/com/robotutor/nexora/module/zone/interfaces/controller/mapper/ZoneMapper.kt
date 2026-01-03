package com.robotutor.nexora.module.zone.interfaces.controller.mapper

import com.robotutor.nexora.module.device.domain.vo.ModelNo
import com.robotutor.nexora.module.zone.application.command.CreateWidgetsCommand
import com.robotutor.nexora.module.zone.application.command.CreateZoneCommand
import com.robotutor.nexora.module.zone.application.command.GetZoneQuery
import com.robotutor.nexora.module.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.module.zone.domain.entity.Widget
import com.robotutor.nexora.module.zone.domain.vo.SliderWidgetMetadata
import com.robotutor.nexora.module.zone.domain.vo.ToggleWidgetMetadata
import com.robotutor.nexora.module.zone.domain.vo.WidgetMetadata
import com.robotutor.nexora.module.zone.interfaces.controller.view.*
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.ZoneId
import com.robotutor.nexora.shared.domain.vo.principal.ActorData

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
            widgetId = widget.widgetId.value,
            feedId = widget.feedId.value,
            name = widget.name.value,
            metadata = toWidgetMetadataResponse(widget.metadata),
            createdAt = widget.createdAt,
            updatedAt = widget.updatedAt,
        )
    }

    private fun toWidgetMetadataResponse(metadata: WidgetMetadata): WidgetMetadataResponse {
        return when (metadata) {
            is SliderWidgetMetadata -> SliderWidgetMetadataResponse(metadata.min, metadata.max, metadata.step)
            is ToggleWidgetMetadata -> ToggleWidgetMetadataResponse()
        }

    }
}