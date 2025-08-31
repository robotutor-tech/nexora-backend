package com.robotutor.nexora.modules.premises.infrastructure.persistence.model

import com.robotutor.nexora.modules.premises.domain.model.Premises
import com.robotutor.nexora.shared.infrastructure.persistence.model.MongoDocument
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
    val street: String,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String,
    val owner: String,
    val createdAt: Instant,
    @Version
    val version: Long? = null
) : MongoDocument<Premises>