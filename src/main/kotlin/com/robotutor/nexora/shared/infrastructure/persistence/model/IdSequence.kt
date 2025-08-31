package com.robotutor.nexora.shared.infrastructure.persistence.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

const val ID_SEQUENCE_COLLECTION = "idSequence"

@Document(ID_SEQUENCE_COLLECTION)
data class IdSequence(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val idType: String,
    val sequence: Long
)