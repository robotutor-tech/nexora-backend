package com.robotutor.nexora.modules.widget.infrastructure.persistence.mapper

import com.robotutor.nexora.modules.widget.domain.entity.Widget
import com.robotutor.nexora.modules.widget.domain.entity.WidgetId
import com.robotutor.nexora.modules.widget.infrastructure.persistence.document.WidgetDocument
import com.robotutor.nexora.shared.domain.model.*
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper
import org.springframework.stereotype.Service

@Service
class WidgetDocumentMapper : DocumentMapper<Widget, WidgetDocument> {
    override fun toMongoDocument(domain: Widget): WidgetDocument = WidgetDocument(
        id = null,
        widgetId = domain.widgetId.value,
        premisesId = domain.premisesId.value,
        name = domain.name.value,
        feedId = domain.feedId.value,
        zoneId = domain.zoneId.value,
        type = domain.type,
        createdAt = domain.createdAt,
        updatedAt = domain.updatedAt,
        version = domain.version
    )

    override fun toDomainModel(document: WidgetDocument): Widget = Widget(
        widgetId = WidgetId(document.widgetId),
        premisesId = PremisesId(document.premisesId),
        name = Name(document.name),
        feedId = FeedId(document.feedId),
        zoneId = ZoneId(document.zoneId),
        type = document.type,
        createdAt = document.createdAt,
        updatedAt = document.updatedAt,
        version = document.version
    )
}
