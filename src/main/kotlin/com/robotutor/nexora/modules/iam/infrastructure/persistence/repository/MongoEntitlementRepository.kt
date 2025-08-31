package com.robotutor.nexora.modules.iam.infrastructure.persistence.repository

import com.robotutor.nexora.modules.iam.infrastructure.persistence.mapper.EntitlementDocumentMapper
import com.robotutor.nexora.modules.iam.infrastructure.persistence.document.EntitlementDocument
import com.robotutor.nexora.modules.iam.domain.model.Entitlement
import com.robotutor.nexora.modules.iam.domain.repository.EntitlementRepository
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ResourceType
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.infrastructure.persistence.repository.MongoRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MongoEntitlementRepository(
    mongoTemplate: ReactiveMongoTemplate,
) : MongoRepository<Entitlement, EntitlementDocument>(mongoTemplate, EntitlementDocument::class.java, EntitlementDocumentMapper()),
    EntitlementRepository {
    override fun save(entitlement: Entitlement): Mono<Entitlement> {
        val query = Query(Criteria.where("entitlementId").`is`(entitlement.entitlementId.value))
        return this.findAndReplace(query, entitlement)
    }

    override fun findAllByPremisesIdAndRoleIdAndResourceTypeAndAction(
        premisesId: PremisesId,
        roleId: RoleId,
        resourceType: ResourceType,
        action: ActionType
    ): Flux<Entitlement> {
        val query = Query(
            Criteria.where("premisesId").`is`(premisesId.value)
                .and("roleId").`is`(roleId.value)
                .and("resourceType").`is`(resourceType)
                .and("action").`is`(action)
        )
        return this.findAll(query)
    }
}
