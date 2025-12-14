package com.robotutor.nexora.context.zone.infrastructure.persistence.document

import com.robotutor.nexora.context.zone.domain.entity.Zone
import com.robotutor.nexora.shared.infrastructure.persistence.document.MongoDocument
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
    val createdBy: String,
    val createdAt: Instant,
    @Version
    val version: Long? = null
) : MongoDocument<Zone>