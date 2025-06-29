package com.robotutor.nexora.zone.models

import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.PremisesActorData
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.ZoneOffset

const val ZONE_COLLECTION = "zones"

@TypeAlias("Zone")
@Document(ZONE_COLLECTION)
data class Zone(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val zoneId: ZoneId,
    val premisesId: PremisesId,
    val name: String,
    val createdBy: ActorId,
    val createdAt: Instant = Instant.now(),
    @Version
    val version: Long? = null
) {
    companion object {
        fun from(zoneId: ZoneId, name: String, actor: PremisesActorData): Zone {
            return Zone(
                zoneId = zoneId,
                name = name,
                premisesId = actor.premisesId,
                createdBy = actor.actorId,
            )
        }
    }
}

typealias ZoneId = String
