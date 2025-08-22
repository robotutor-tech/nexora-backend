package com.robotutor.nexora.modules.iam.adapters.repository

import com.robotutor.nexora.modules.iam.adapters.model.EntitlementDocument
import com.robotutor.nexora.modules.iam.domain.model.Entitlement
import com.robotutor.nexora.modules.iam.domain.repository.EntitlementRepository
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ResourceType
import com.robotutor.nexora.shared.domain.model.RoleId
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class MongoEntitlementDocumentRepository(private val entitlementDocumentRepository: EntitlementDocumentRepository) :
    EntitlementRepository {
    override fun save(entitlement: Entitlement): Mono<Entitlement> {
        return entitlementDocumentRepository.save(EntitlementDocument.from(entitlement))
            .map { it.toDomainModel() }
    }

    override fun findAllByPremisesIdAndRoleIdAndResourceTypeAndAction(
        premisesId: PremisesId,
        roleId: RoleId,
        resourceType: ResourceType,
        action: ActionType
    ): Flux<Entitlement> {
        return entitlementDocumentRepository.findAllByPremisesIdAndRoleIdAndResourceTypeAndActionAndStatus(
            premisesId = premisesId.value,
            roleId = roleId.value,
            resourceType = resourceType,
            action = if (action == ActionType.LIST) ActionType.READ else action,
        )
            .map { it.toDomainModel() }

    }
}