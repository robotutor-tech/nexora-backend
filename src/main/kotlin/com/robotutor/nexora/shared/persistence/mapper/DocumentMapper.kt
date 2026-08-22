package com.robotutor.nexora.shared.persistence.mapper

import com.robotutor.nexora.shared.domain.Aggregate
import com.robotutor.nexora.shared.persistence.document.MongoDocument

interface DocumentMapper<D : Aggregate, M : MongoDocument<D>> {
    fun toDocument(domain: D): M
    fun toDomain(document: M): D
}
