package com.robotutor.nexora.modules.widget.interfaces.controller.mapper

import com.robotutor.nexora.modules.widget.domain.model.Widget
import com.robotutor.nexora.modules.widget.interfaces.controller.dto.WidgetResponse

class WidgetMapper {
    companion object {
        fun toWidgetResponse(widget: Widget): WidgetResponse {
            return WidgetResponse(
                widgetId = widget.widgetId.value,
                premisesId = widget.premisesId.value,
                name = widget.name,
                feedId = widget.feedId.value,
                type = widget.type,
                zoneId = widget.zoneId.value,
            )
        }
    }
}