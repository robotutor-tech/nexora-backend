package com.robotutor.nexora.shared.infrastructure.persistence.repository

import com.robotutor.nexora.shared.domain.event.DomainModel
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper
import com.robotutor.nexora.shared.infrastructure.persistence.document.MongoDocument
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.mongodb.core.FindAndReplaceOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

open class MongoRepository<D : DomainModel, T : MongoDocument<D>>(
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
            .retryOptimisticLockingFailure()
            .map { mapper.toDomainModel(it) }
    }

    fun deleteOne(query: Query): Mono<D> {
        return mongoTemplate.findAndRemove(query, entityType)
            .retryOptimisticLockingFailure()
            .map { mapper.toDomainModel(it) }
    }
}


fun <T> Mono<T>.retryOptimisticLockingFailure(): Mono<T> {
    return retryWhen(
        Retry.fixedDelay(5, Duration.ofMillis(500))
            .filter { it is OptimisticLockingFailureException })
}