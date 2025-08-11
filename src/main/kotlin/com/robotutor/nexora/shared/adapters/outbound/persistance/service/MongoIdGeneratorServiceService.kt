package com.robotutor.nexora.shared.adapters.outbound.persistance.service

import com.robotutor.nexora.shared.adapters.outbound.persistance.model.ID_SEQUENCE_COLLECTION
import com.robotutor.nexora.shared.adapters.outbound.persistance.model.IdSequence
import com.robotutor.nexora.shared.domain.model.IdSequenceType
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.utils.retryOptimisticLockingFailure
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono


@Repository
class MongoIdGeneratorServiceService(private val reactiveMongoTemplate: ReactiveMongoTemplate) : IdGeneratorService {
    override fun generateId(idType: IdSequenceType): Mono<String> {
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