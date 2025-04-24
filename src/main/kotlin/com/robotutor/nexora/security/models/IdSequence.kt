package com.robotutor.nexora.security.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

const val ID_SEQUENCE_COLLECTION = "idSequence"

@Document(ID_SEQUENCE_COLLECTION)
data class IdSequence(@Id val idType: String, val sequence: Long)