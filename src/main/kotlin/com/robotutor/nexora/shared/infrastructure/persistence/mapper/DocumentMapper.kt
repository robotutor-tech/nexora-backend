package com.robotutor.nexora.shared.infrastructure.persistence.mapper

import com.robotutor.nexora.shared.domain.Aggregate
import com.robotutor.nexora.shared.infrastructure.persistence.document.MongoDocument

interface DocumentMapper<D : Aggregate, M : MongoDocument<D>> {
    fun toMongoDocument(domain: D): M
    fun toDomainModel(document: M): D
}
