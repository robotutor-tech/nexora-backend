package com.robotutor.nexora.common.persistence.mapper

import com.robotutor.nexora.shared.domain.Aggregate
import com.robotutor.nexora.common.persistence.document.MongoDocument

interface DocumentMapper<D : Aggregate, M : MongoDocument<D>> {
    fun toMongoDocument(domain: D): M
    fun toDomainModel(document: M): D
}
