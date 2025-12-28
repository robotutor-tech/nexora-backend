package com.robotutor.nexora.common.persistence.mongo.service

import com.robotutor.nexora.common.persistence.mongo.document.IdSequenceType
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.common.persistence.mongo.document.IdSequenceDocument
import com.robotutor.nexora.common.persistence.mongo.repository.retryOptimisticLockingFailure
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.findAndModify
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono


@Repository
class MongoIdGeneratorService(private val mongoTemplate: ReactiveMongoTemplate) : IdGeneratorService {

    override fun generateId(idType: IdSequenceType): Mono<String> {
        return mongoTemplate.findAndModify<IdSequenceDocument>(
            Query.query(Criteria.where("idType").`is`(idType.name)),
            Update().inc("sequence", 1),
            FindAndModifyOptions.options().returnNew(true).upsert(true),
        )
            .retryOptimisticLockingFailure()
            .map { idSequence ->
                idSequence.sequence.toString().padStart(idType.length, '0')
            }
    }
}