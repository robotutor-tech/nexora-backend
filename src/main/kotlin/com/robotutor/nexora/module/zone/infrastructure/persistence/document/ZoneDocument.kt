package com.robotutor.nexora.module.zone.infrastructure.persistence.document

import com.robotutor.nexora.module.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.common.persistence.document.MongoDocument
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val ZONE_COLLECTION = "zones"

@TypeAlias("Zone")
@Document(ZONE_COLLECTION)
data class ZoneDocument(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val zoneId: String,
    @Indexed
    val premisesId: String,
    val name: String,
    val widgets: List<WidgetDocument>,
    val createdBy: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    @Version
    val version: Long? = null
) : MongoDocument<ZoneAggregate>

