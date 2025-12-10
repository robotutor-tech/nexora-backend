package com.robotutor.nexora.modules.widget.infrastructure.persistence.document

import com.robotutor.nexora.modules.widget.domain.entity.Widget
import com.robotutor.nexora.modules.widget.domain.entity.WidgetType
import com.robotutor.nexora.shared.infrastructure.persistence.document.MongoDocument
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
    val version: Long = 0
) : MongoDocument<Widget>