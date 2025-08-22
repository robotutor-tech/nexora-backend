package com.robotutor.nexora.modules.iam.services

import com.robotutor.nexora.shared.logger.Logger
import org.springframework.stereotype.Service

@Service
class EntitlementService(
) {
    private val logger = Logger(this::class.java)

//    fun createEntitlement(
//        request: EntitlementRequest,
//        premisesId: PremisesId,
//    ): Mono<EntitlementDocument> {
//        return idGeneratorService.generateId(IdType.ENTITLEMENT_ID)
//            .map { entitlementId -> EntitlementDocument.from(entitlementId, premisesId, request) }
//            .flatMap {
//                entitlementRepository.save(it)
//            }
//            .logOnSuccess(logger, "Successfully created new Entitlement")
//            .logOnError(logger, "", "Failed to create new Entitlement")
//    }
//
//    fun getEntitlements(
//        resourceType: ResourceType, action: ActionType, premisesActorData: PremisesActorData
//    ): Flux<EntitlementDocument> {
//        return entitlementRepository.findAllByPremisesIdAndRoleIdAndResourceTypeAndActionAndStatus(
//            premisesId = premisesActorData.premisesId,
//            roleId = premisesActorData.roleDocument.roleId,
//            resourceType = resourceType,
//            action = if (action == ActionType.LIST) ActionType.READ else action,
//        )
//    }
//
//    fun createAndAssignNewResource(resource: EntitlementResource, premisesActorData: PremisesActorData): Flux<RoleDocument> {
//        return roleService.getHumanRolesWithCurrent(premisesActorData)
//            .flatMap { role -> createEntitlement(ActionType.READ, role, resource, premisesActorData).map { role } }
//            .flatMap { role ->
//                if (resource.resourceType == ResourceType.FEED)
//                    createEntitlement(ActionType.CONTROL, role, resource, premisesActorData).map { role }
//                else createMono(role)
//            }
//            .filter { role -> role.role == RoleType.ADMIN || role.role == RoleType.OWNER }
//            .flatMap { role -> createEntitlement(ActionType.UPDATE, role, resource, premisesActorData).map { role } }
//            .flatMap { role -> createEntitlement(ActionType.DELETE, role, resource, premisesActorData).map { role } }
//    }
//
//    private fun createEntitlement(
//        action: ActionType, roleDocument: RoleDocument, resource: EntitlementResource, premisesActorData: PremisesActorData
//    ): Mono<EntitlementDocument> {
//        val entitlementRequest = EntitlementRequest(
//            action = action,
//            resourceType = resource.resourceType,
//            resourceId = resource.resourceId,
//            roleId = roleDocument.roleId
//        )
//        return createEntitlement(entitlementRequest, premisesActorData.premisesId)
//    }
}
