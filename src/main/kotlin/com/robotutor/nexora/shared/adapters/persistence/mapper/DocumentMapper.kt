package com.robotutor.nexora.shared.adapters.persistence.mapper

import com.robotutor.nexora.shared.adapters.persistence.model.MongoDocument
import com.robotutor.nexora.shared.domain.event.DomainModel

interface DocumentMapper<D : DomainModel, M : MongoDocument<D>> {
    fun toMongoDocument(domain: D): M
    fun toDomainModel(document: M): D
}
