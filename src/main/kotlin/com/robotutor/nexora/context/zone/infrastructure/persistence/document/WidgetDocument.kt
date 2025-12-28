package com.robotutor.nexora.context.zone.infrastructure.persistence.document

import com.robotutor.nexora.context.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.common.persistence.document.MongoDocument
import java.time.Instant

data class WidgetDocument(
    val widgetId: String,
    val name: String,
    val feedId: String,
    val metadata: Map<Any, Any>,
    val createdAt: Instant,
    val updatedAt: Instant,
) : MongoDocument<ZoneAggregate>

