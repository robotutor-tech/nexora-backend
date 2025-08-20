package com.robotutor.nexora.modules.iam.services

import com.robotutor.nexora.modules.iam.controllers.view.EntitlementRequest
import com.robotutor.nexora.modules.iam.models.Entitlement
import com.robotutor.nexora.modules.iam.models.IdType
import com.robotutor.nexora.modules.iam.models.Role
import com.robotutor.nexora.modules.iam.models.RoleType
import com.robotutor.nexora.modules.iam.repositories.EntitlementRepository
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.application.annotations.ActionType
import com.robotutor.nexora.common.security.application.annotations.ResourceType
import com.robotutor.nexora.common.security.models.PremisesActorData
import com.robotutor.nexora.common.security.service.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class EntitlementService(
    private val idGeneratorService: IdGeneratorService,
    private val entitlementRepository: EntitlementRepository,
    private val roleService: RoleService
) {
    private val logger = Logger(this::class.java)

    fun createEntitlement(
        request: EntitlementRequest,
        premisesId: PremisesId,
    ): Mono<Entitlement> {
        return idGeneratorService.generateId(IdType.ENTITLEMENT_ID)
            .map { entitlementId -> Entitlement.from(entitlementId, premisesId, request) }
            .flatMap {
                entitlementRepository.save(it)
            }
            .logOnSuccess(logger, "Successfully created new Entitlement")
            .logOnError(logger, "", "Failed to create new Entitlement")
    }

    fun getEntitlements(
        resourceType: ResourceType, action: ActionType, premisesActorData: PremisesActorData
    ): Flux<Entitlement> {
        return entitlementRepository.findAllByPremisesIdAndRoleIdAndResourceTypeAndActionAndStatus(
            premisesId = premisesActorData.premisesId,
            roleId = premisesActorData.role.roleId,
            resourceType = resourceType,
            action = if (action == ActionType.LIST) ActionType.READ else action,
        )
    }

    fun createAndAssignNewResource(resource: EntitlementResource, premisesActorData: PremisesActorData): Flux<Role> {
        return roleService.getHumanRolesWithCurrent(premisesActorData)
            .flatMap { role -> createEntitlement(ActionType.READ, role, resource, premisesActorData).map { role } }
            .flatMap { role ->
                if (resource.resourceType == ResourceType.FEED)
                    createEntitlement(ActionType.CONTROL, role, resource, premisesActorData).map { role }
                else createMono(role)
            }
            .filter { role -> role.role == RoleType.ADMIN || role.role == RoleType.OWNER }
            .flatMap { role -> createEntitlement(ActionType.UPDATE, role, resource, premisesActorData).map { role } }
            .flatMap { role -> createEntitlement(ActionType.DELETE, role, resource, premisesActorData).map { role } }
    }

    private fun createEntitlement(
        action: ActionType, role: Role, resource: EntitlementResource, premisesActorData: PremisesActorData
    ): Mono<Entitlement> {
        val entitlementRequest = EntitlementRequest(
            action = action,
            resourceType = resource.resourceType,
            resourceId = resource.resourceId,
            roleId = role.roleId
        )
        return createEntitlement(entitlementRequest, premisesActorData.premisesId)
    }
}
