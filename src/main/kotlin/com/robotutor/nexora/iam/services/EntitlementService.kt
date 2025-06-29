package com.robotutor.nexora.iam.services

import com.robotutor.nexora.iam.controllers.view.EntitlementRequest
import com.robotutor.nexora.iam.models.Entitlement
import com.robotutor.nexora.iam.models.IdType
import com.robotutor.nexora.iam.models.Role
import com.robotutor.nexora.iam.models.RoleType
import com.robotutor.nexora.iam.repositories.EntitlementRepository
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.createMono
import com.robotutor.nexora.security.filters.annotations.ActionType
import com.robotutor.nexora.security.filters.annotations.ResourceType
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.services.IdGeneratorService
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
