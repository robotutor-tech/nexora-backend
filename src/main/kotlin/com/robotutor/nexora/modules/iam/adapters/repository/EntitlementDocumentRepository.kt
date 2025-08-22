package com.robotutor.nexora.modules.iam.adapters.repository

import com.robotutor.nexora.modules.iam.adapters.model.EntitlementDocument
import com.robotutor.nexora.modules.iam.domain.model.Entitlement
import com.robotutor.nexora.modules.iam.domain.model.EntitlementStatus
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.ResourceType
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface EntitlementDocumentRepository : ReactiveCrudRepository<EntitlementDocument, String> {
    fun findAllByPremisesIdAndRoleIdAndResourceTypeAndActionAndStatus(
        premisesId: String,
        roleId: String,
        resourceType: ResourceType,
        action: ActionType,
        status: EntitlementStatus = EntitlementStatus.ACTIVE
    ): Flux<EntitlementDocument>
}