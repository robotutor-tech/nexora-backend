package com.robotutor.nexora.shared.infrastructure.persistence.service

import com.robotutor.nexora.shared.domain.model.IdSequenceType
import com.robotutor.nexora.shared.domain.model.SequenceId
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.shared.infrastructure.persistence.document.IdSequence
import com.robotutor.nexora.shared.infrastructure.persistence.repository.retryOptimisticLockingFailure
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono


@Repository
class MongoIdGeneratorService(private val mongoTemplate: ReactiveMongoTemplate) : IdGeneratorService {
    override fun <T : SequenceId> generateId(idType: IdSequenceType, clazz: Class<T>): Mono<T> {
        return mongoTemplate.findAndModify(
            Query.query(Criteria.where("idType").`is`(idType.name)),
            Update().inc("sequence", 1),
            FindAndModifyOptions.options().returnNew(true).upsert(true),
            IdSequence::class.java,
        )
            .retryOptimisticLockingFailure()
            .map { idSequence ->
                idSequence.sequence.toString().padStart(idType.length, '0')
            }
            .map {
                clazz.getDeclaredConstructor(String::class.java).newInstance(it)
            }
    }

    override fun generateId(idType: IdSequenceType): Mono<String> {
        return mongoTemplate.findAndModify(
            Query.query(Criteria.where("idType").`is`(idType.name)),
            Update().inc("sequence", 1),
            FindAndModifyOptions.options().returnNew(true).upsert(true),
            IdSequence::class.java,
        )
            .retryOptimisticLockingFailure()
            .map { idSequence ->
                idSequence.sequence.toString().padStart(idType.length, '0')
            }
    }
}