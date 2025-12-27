package com.robotutor.nexora.shared.infrastructure.persistence.document

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

const val ID_SEQUENCE_COLLECTION = "idSequence"

@Document(ID_SEQUENCE_COLLECTION)
data class IdSequenceDocument(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val idType: String,
    val sequence: Long,
    @Version
    val version: Long? = null
)