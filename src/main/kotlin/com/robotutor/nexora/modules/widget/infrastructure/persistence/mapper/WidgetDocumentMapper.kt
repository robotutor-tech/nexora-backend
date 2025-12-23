package com.robotutor.nexora.modules.widget.infrastructure.persistence.mapper

import com.robotutor.nexora.modules.widget.domain.entity.Widget
import com.robotutor.nexora.modules.widget.domain.entity.WidgetId
import com.robotutor.nexora.modules.widget.infrastructure.persistence.document.WidgetDocument
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ZoneId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper

object WidgetDocumentMapper : DocumentMapper<Widget, WidgetDocument> {
    override fun toMongoDocument(domain: Widget): WidgetDocument {
        return WidgetDocument(
            id = domain.getObjectId(),
            widgetId = domain.widgetId.value,
            premisesId = domain.premisesId.value,
            name = domain.name.value,
            feedId = domain.feedId.value,
            zoneId = domain.zoneId.value,
            type = domain.type,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            version = domain.getVersion()
        )
    }

    override fun toDomainModel(document: WidgetDocument): Widget {
        return Widget(
            widgetId = WidgetId(document.widgetId),
            premisesId = PremisesId(document.premisesId),
            name = Name(document.name),
            feedId = FeedId(document.feedId),
            zoneId = ZoneId(document.zoneId),
            type = document.type,
            createdAt = document.createdAt,
            updatedAt = document.updatedAt,
        ).setObjectIdAndVersion(document.id, document.version)
    }
}
