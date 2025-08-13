package com.robotutor.nexora.modules.widget.models

import com.robotutor.nexora.modules.feed.models.FeedId
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.common.security.models.PremisesActorData
import com.robotutor.nexora.modules.widget.controllers.view.WidgetRequest
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val WIDGET_COLLECTION = "widgets"

@TypeAlias("Widget")
@Document(WIDGET_COLLECTION)
data class Widget(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val widgetId: WidgetId,
    @Indexed
    val premisesId: PremisesId,
    val name: String,
    val feedId: FeedId,
    val zoneId: String,
    val type: WidgetType,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    @Version
    val version: Long? = null
) {
    companion object {
        fun from(widgetId: WidgetId, widgetRequest: WidgetRequest, premisesActorData: PremisesActorData): Widget {
            return Widget(
                widgetId = widgetId,
                premisesId = premisesActorData.premisesId,
                name = widgetRequest.name,
                feedId = widgetRequest.feed,
                type = widgetRequest.type,
                zoneId = widgetRequest.zoneId
            )
        }
    }
}

enum class WidgetType {
    TOGGLE,
    SLIDER
}

typealias WidgetId = String