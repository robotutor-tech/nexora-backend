package com.robotutor.nexora.modules.zone.infrastructure.persistence.document

import com.robotutor.nexora.modules.zone.domain.model.Zone
import com.robotutor.nexora.shared.infrastructure.persistence.model.MongoDocument
import org.bson.types.ObjectId
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
    var id: ObjectId? = null,
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