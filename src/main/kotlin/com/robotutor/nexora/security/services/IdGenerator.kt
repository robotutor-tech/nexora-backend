package com.robotutor.nexora.security.services

import com.robotutor.nexora.security.models.ID_SEQUENCE_COLLECTION
import com.robotutor.nexora.security.models.IdSequence
import com.robotutor.nexora.utils.retryOptimisticLockingFailure
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

interface IdSequenceType {
    val length: Int
}

@Service
class IdGeneratorService(private val reactiveMongoTemplate: ReactiveMongoTemplate) {
    fun generateId(idType: IdSequenceType): Mono<String> {
        return reactiveMongoTemplate.findAndModify(
            Query.query(Criteria.where("idType").`is`(idType)),
            Update().inc("sequence", 1),
            FindAndModifyOptions.options().returnNew(true).upsert(true),
            IdSequence::class.java,
            ID_SEQUENCE_COLLECTION
        )
            .retryOptimisticLockingFailure()
            .map { idSequence ->
            idSequence.sequence.toString().padStart(idType.length, '0')
        }
    }
}