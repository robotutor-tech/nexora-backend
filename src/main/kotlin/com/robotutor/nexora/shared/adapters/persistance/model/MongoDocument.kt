package com.robotutor.nexora.shared.adapters.persistance.model

interface MongoDocument<DomainModel> {
    fun toDomainModel(): DomainModel
}