package com.robotutor.nexora.shared.adapters.persistence.repository

import com.robotutor.nexora.shared.adapters.persistence.mapper.DocumentMapper
import com.robotutor.nexora.shared.adapters.persistence.model.MongoDocument
import com.robotutor.nexora.shared.domain.event.DomainModel
import org.springframework.data.mongodb.core.FindAndReplaceOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

open class BaseDocumentRepository<D : DomainModel, T : MongoDocument<D>>(
    private val mongoTemplate: ReactiveMongoTemplate,
    private val entityType: Class<T>,
    private val mapper: DocumentMapper<D, T>
) {

    fun findOne(query: Query): Mono<D> {
        return mongoTemplate.findOne(query, entityType)
            .map { mapper.toDomainModel(it) }
    }

    fun findAll(query: Query): Flux<D> {
        return mongoTemplate.find(query, entityType)
            .map { mapper.toDomainModel(it) }
    }

    fun findAndReplace(query: Query, replacement: D): Mono<D> {
        val document = mapper.toMongoDocument(replacement)
        return mongoTemplate.findAndReplace(query, document, FindAndReplaceOptions().returnNew().upsert())
            .map { replacement }
    }

    fun deleteOne(query: Query): Mono<D> {
        return mongoTemplate.findAndRemove(query, entityType)
            .map { mapper.toDomainModel(it) }
    }
}