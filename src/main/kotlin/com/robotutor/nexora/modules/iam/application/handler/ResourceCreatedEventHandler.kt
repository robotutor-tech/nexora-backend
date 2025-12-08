//package com.robotutor.nexora.modules.iam.application.handler
//
//import com.robotutor.nexora.common.security.createMono
//import com.robotutor.nexora.modules.iam.application.EntitlementUseCase
//import com.robotutor.nexora.modules.iam.application.RoleUseCase
//import com.robotutor.nexora.modules.iam.application.command.CreateEntitlementCommand
//import com.robotutor.nexora.modules.iam.domain.entity.Role
//import com.robotutor.nexora.shared.domain.event.ResourceCreatedEvent
//import com.robotutor.nexora.shared.domain.model.ActionType
//import com.robotutor.nexora.shared.domain.model.ActorData
//import com.robotutor.nexora.shared.domain.model.ResourceType
//import com.robotutor.nexora.shared.domain.model.RoleType
//import com.robotutor.nexora.shared.logger.Logger
//import com.robotutor.nexora.shared.logger.logOnError
//import com.robotutor.nexora.shared.logger.logOnSuccess
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Mono
//
//@Service
//class ResourceCreatedEventHandlerUserCase(
//    private val entitlementUseCase: EntitlementUseCase,
//    private val roleUseCase: RoleUseCase
//) {
//
//    private val logger = Logger(this::class.java)
//
//    fun createResource(event: ResourceCreatedEvent, actorData: ActorData): Mono<List<Role>> {
//        return roleUseCase.getHumanRolesWithCurrentRole(actorData.premisesId, actorData)
//            .flatMap { role -> createEntitlement(ActionType.READ, role, event) }
//            .flatMap { role ->
//                if (event.resourceType == ResourceType.FEED) {
//                    createEntitlement(ActionType.CONTROL, role, event)
//                } else {
//                    createMono(role)
//                }
//            }
//            .filter { role -> role.roleType == RoleType.ADMIN || role.roleType == RoleType.OWNER }
//            .flatMap { role -> createEntitlement(ActionType.UPDATE, role, event) }
//            .flatMap { role -> createEntitlement(ActionType.DELETE, role, event) }
//            .collectList()
//            .logOnSuccess(logger, "Successfully handled resource created event")
//            .logOnError(logger, "", "Failed to handle resource created event")
//    }
//
//    private fun createEntitlement(
//        action: ActionType,
//        role: Role,
//        event: ResourceCreatedEvent
//    ): Mono<Role> {
//        val command = CreateEntitlementCommand(
//            premisesId = role.premisesId,
//            roleId = role.roleId,
//            resourceId = event.resourceId,
//            resourceType = event.resourceType,
//            action = action
//        )
//        return entitlementUseCase.createEntitlement(command)
//            .map { role }
//    }
//
//}