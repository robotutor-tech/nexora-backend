package com.robotutor.nexora.modules.zone.adapters.persistance.model

import com.robotutor.nexora.modules.zone.domain.model.Zone
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
) {
    companion object {
        fun from(zone: Zone): ZoneDocument {
            return ZoneDocument(
                zoneId = zone.zoneId.value,
                name = zone.name,
                premisesId = zone.premisesId.value,
                createdBy = zone.createdBy.value,
                createdAt = zone.createdAt,
            )
        }
    }
}

