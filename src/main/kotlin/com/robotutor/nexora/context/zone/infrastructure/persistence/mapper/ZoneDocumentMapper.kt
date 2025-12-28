package com.robotutor.nexora.context.zone.infrastructure.persistence.mapper

import com.robotutor.nexora.context.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.context.zone.domain.entity.Widget
import com.robotutor.nexora.context.zone.domain.vo.ToggleWidgetMetadata
import com.robotutor.nexora.context.zone.domain.vo.WidgetId
import com.robotutor.nexora.context.zone.infrastructure.persistence.document.WidgetDocument
import com.robotutor.nexora.shared.domain.vo.ZoneId
import com.robotutor.nexora.context.zone.infrastructure.persistence.document.ZoneDocument
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.common.persistence.mapper.DocumentMapper

object ZoneDocumentMapper : DocumentMapper<ZoneAggregate, ZoneDocument> {

    override fun toDomainModel(document: ZoneDocument): ZoneAggregate {
        return ZoneAggregate.create(
            zoneId = ZoneId(document.zoneId),
            premisesId = PremisesId(document.premisesId),
            name = Name(document.name),
            createdBy = ActorId(document.createdBy),
            widgets = document.widgets.map { toWidgetDomain(it) },
            createdAt = document.createdAt,
            updatedAt = document.updatedAt
        ).setObjectIdAndVersion(document.id, document.version)
    }


    override fun toMongoDocument(domain: ZoneAggregate): ZoneDocument {
        return ZoneDocument(
            id = domain.getObjectId(),
            zoneId = domain.zoneId.value,
            name = domain.name.value,
            premisesId = domain.premisesId.value,
            createdBy = domain.createdBy.value,
            createdAt = domain.createdAt,
            updatedAt = domain.getUpdatedAt(),
            widgets = domain.getWidgets().map { toWidgetDocument(it) },
            version = domain.getVersion(),
        )
    }

    private fun toWidgetDomain(document: WidgetDocument): Widget {
        return Widget.create(
            widgetId = WidgetId(document.widgetId),
            name = Name(document.name),
            feedId = FeedId(document.feedId),
            metadata = ToggleWidgetMetadata(),
            createdAt = document.createdAt,
            updatedAt = document.updatedAt
        )
    }

    private fun toWidgetDocument(widget: Widget): WidgetDocument {
        return WidgetDocument(
            widgetId = widget.widgetId.value,
            name = widget.name.value,
            feedId = widget.feedId.value,
            metadata = emptyMap(),
            createdAt = widget.createdAt,
            updatedAt = widget.updatedAt
        )
    }
}