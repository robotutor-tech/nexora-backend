package com.robotutor.nexora.modules.widget.adapters.persistence.mapper

import com.robotutor.nexora.modules.widget.adapters.persistence.model.WidgetDocument
import com.robotutor.nexora.modules.widget.domain.model.Widget
import com.robotutor.nexora.shared.adapters.persistence.mapper.DocumentMapper
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.WidgetId
import com.robotutor.nexora.shared.domain.model.ZoneId
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

