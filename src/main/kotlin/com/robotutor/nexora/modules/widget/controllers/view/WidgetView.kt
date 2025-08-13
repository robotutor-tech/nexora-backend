package com.robotutor.nexora.modules.widget.controllers.view

import com.robotutor.nexora.modules.feed.models.FeedId
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.modules.widget.models.Widget
import com.robotutor.nexora.modules.widget.models.WidgetId
import com.robotutor.nexora.modules.widget.models.WidgetType
import jakarta.validation.constraints.NotBlank

data class WidgetRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    @field:NotBlank(message = "Feed is required")
    val feed: FeedId,
    val type: WidgetType,
    @field:NotBlank(message = "Zone is required")
    val zoneId: String
) {
}

data class WidgetView(
    val widgetId: WidgetId,
    val premisesId: PremisesId,
    val name: String,
    val feedId: FeedId,
    val type: WidgetType,
    val zoneId: String,
) {
    companion object {
        fun from(widget: Widget): WidgetView {
            return WidgetView(
                widgetId = widget.widgetId,
                premisesId = widget.premisesId,
                name = widget.name,
                feedId = widget.feedId,
                type = widget.type,
                zoneId = widget.zoneId,
            )
        }
    }
}
