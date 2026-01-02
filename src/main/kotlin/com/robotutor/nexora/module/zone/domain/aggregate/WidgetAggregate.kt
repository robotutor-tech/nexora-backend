package com.robotutor.nexora.module.zone.domain.aggregate

import com.robotutor.nexora.module.zone.domain.vo.WidgetId
import com.robotutor.nexora.module.zone.domain.vo.WidgetMetadata
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ZoneId
import java.time.Instant

class WidgetAggregate private constructor(
    val widgetId: WidgetId,
    val premisesId: PremisesId,
    val name: Name,
    val feedId: FeedId,
    val zoneId: ZoneId,
    val metaData: WidgetMetadata,
    val createdAt: Instant,
    val updatedAt: Instant,
) {

    companion object {
        fun register(
            premisesId: PremisesId,
            name: Name,
            feedId: FeedId,
            zoneId: ZoneId,
            metaData: WidgetMetadata
        ): WidgetAggregate {
            return create(WidgetId.generate(), premisesId, name, feedId, zoneId, metaData)
        }

        fun create(
            widgetId: WidgetId,
            premisesId: PremisesId,
            name: Name,
            feedId: FeedId,
            zoneId: ZoneId,
            metaData: WidgetMetadata,
            createdAt: Instant = Instant.now(),
            updatedAt: Instant = Instant.now(),
        ): WidgetAggregate {
            return WidgetAggregate(
                widgetId,
                premisesId,
                name,
                feedId,
                zoneId,
                metaData,
                createdAt,
                updatedAt,
            )
        }
    }
}

