package com.robotutor.nexora.modules.widget.interfaces.controller.mapper

import com.robotutor.nexora.modules.widget.domain.entity.Widget
import com.robotutor.nexora.modules.widget.interfaces.controller.dto.WidgetResponse

object WidgetMapper {
    fun toWidgetResponse(widget: Widget): WidgetResponse {
        return WidgetResponse(
            widgetId = widget.widgetId.value,
            premisesId = widget.premisesId.value,
            name = widget.name.value,
            feedId = widget.feedId.value,
            type = widget.type,
            zoneId = widget.zoneId.value,
        )
    }
}