package com.robotutor.nexora.context.iam.infrastructure.persistence.repository

import com.robotutor.nexora.context.iam.domain.aggregate.GroupAggregate
import com.robotutor.nexora.context.iam.domain.repository.GroupRepository
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.GroupDocument
import com.robotutor.nexora.context.iam.infrastructure.persistence.mapper.GroupDocumentMapper
import com.robotutor.nexora.shared.infrastructure.persistence.repository.MongoRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MongoGroupRepository(
    mongoTemplate: ReactiveMongoTemplate,
) : MongoRepository<GroupAggregate, GroupDocument>(mongoTemplate, GroupDocument::class.java, GroupDocumentMapper),
    GroupRepository {
    override fun save(groupAggregate: GroupAggregate): Mono<GroupAggregate> {
        val query = Query(Criteria.where("groupId").`is`(groupAggregate.groupId.value))
        return this.findAndReplace(query, groupAggregate)
    }
}