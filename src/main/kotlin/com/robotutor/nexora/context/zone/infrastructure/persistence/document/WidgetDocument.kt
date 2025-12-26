package com.robotutor.nexora.context.zone.infrastructure.persistence.document

import com.robotutor.nexora.context.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.shared.infrastructure.persistence.document.MongoDocument
import org.springframework.data.mongodb.core.index.Indexed
import java.time.Instant

data class WidgetDocument(
    @Indexed(unique = true)
    val widgetId: String,
    val name: String,
    val feedId: String,
    val metadata: Map<Any, Any>,
    val createdAt: Instant,
    val updatedAt: Instant,
) : MongoDocument<ZoneAggregate>

