package com.robotutor.nexora.modules.widget.adapters.persistance.model

import com.robotutor.nexora.modules.widget.domain.model.Widget
import com.robotutor.nexora.modules.widget.domain.model.WidgetType
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.WidgetId
import com.robotutor.nexora.shared.domain.model.ZoneId
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
data class WidgetDocument(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val widgetId: String,
    @Indexed
    val premisesId: String,
    val name: String,
    val feedId: String,
    val zoneId: String,
    val type: WidgetType,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    @Version
    val version: Long? = null
) {
    fun toDomainModel(): Widget {
        return Widget(
            widgetId = WidgetId(widgetId),
            premisesId = PremisesId(premisesId),
            name = name,
            feedId = FeedId(feedId),
            zoneId = ZoneId(zoneId),
            type = type,
            createdAt = createdAt,
            updatedAt = updatedAt,
            version = version
        )
    }
}