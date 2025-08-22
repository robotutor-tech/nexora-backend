package com.robotutor.nexora.modules.premises.adapters.persistance.model

import com.robotutor.nexora.modules.premises.domain.model.Premises
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.UserId
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val PREMISES_COLLECTION = "premises"

@TypeAlias("Premises")
@Document(PREMISES_COLLECTION)
data class PremisesDocument(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val premisesId: String,
    val name: String,
    val owner: String,
    val createdAt: Instant,
    @Version
    val version: Long? = null
) {
    fun toDomainModel(): Premises {
        return Premises(
            premisesId = PremisesId(premisesId),
            name = name,
            owner = UserId(owner),
            createdAt = createdAt,
            version = version
        )
    }

    companion object {
        fun from(premises: Premises): PremisesDocument {
            return PremisesDocument(
                premisesId = premises.premisesId.value,
                name = premises.name,
                owner = premises.owner.value,
                createdAt = premises.createdAt,
                version = premises.version,
            )
        }
    }
}
