package com.robotutor.nexora.premises.models

import com.robotutor.nexora.security.models.UserId
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.ZoneOffset

const val PREMISES_COLLECTION = "premises"

@TypeAlias("Premises")
@Document(PREMISES_COLLECTION)
data class Premises(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val premisesId: PremisesId,
    val name: String,
    val createdBy: UserId,
    val createdAt: Instant = Instant.now(),
    @Version
    val version: Long? = null
) {
    companion object {
        fun from(premisesId: PremisesId, name: String, userId: UserId): Premises {
            return Premises(
                premisesId = premisesId,
                name = name,
                createdBy = userId,
            )
        }
    }
}

typealias PremisesId = String
