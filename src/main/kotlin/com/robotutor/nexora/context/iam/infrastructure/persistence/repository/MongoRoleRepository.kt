package com.robotutor.nexora.context.iam.infrastructure.persistence.repository

import com.robotutor.nexora.context.iam.domain.aggregate.RoleAggregate
import com.robotutor.nexora.context.iam.domain.repository.RoleRepository
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.RoleDocument
import com.robotutor.nexora.context.iam.infrastructure.persistence.mapper.RoleDocumentMapper
import com.robotutor.nexora.shared.infrastructure.persistence.repository.MongoRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MongoRoleRepository(
    mongoTemplate: ReactiveMongoTemplate,
) : MongoRepository<RoleAggregate, RoleDocument>(mongoTemplate, RoleDocument::class.java, RoleDocumentMapper),
    RoleRepository {
    override fun save(roleAggregate: RoleAggregate): Mono<RoleAggregate> {
        val query = Query(Criteria.where("roleId").`is`(roleAggregate.roleId.value))
        return this.findAndReplace(query, roleAggregate)
    }
}