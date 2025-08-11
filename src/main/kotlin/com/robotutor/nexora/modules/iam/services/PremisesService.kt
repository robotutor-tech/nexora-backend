package com.robotutor.nexora.modules.iam.services

import com.robotutor.nexora.modules.iam.controllers.view.*
import com.robotutor.nexora.modules.iam.models.*
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.common.security.createFlux
import com.robotutor.nexora.common.security.filters.annotations.ActionType
import com.robotutor.nexora.common.security.filters.annotations.ResourceType
import com.robotutor.nexora.common.security.models.ActorIdentifier
import com.robotutor.nexora.common.security.models.AuthUserData
import com.robotutor.nexora.common.security.models.Identifier
import com.robotutor.nexora.common.security.models.InvitationData
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service("IAMPremisesService")
class PremisesService(
    private val roleService: RoleService,
    private val actorService: ActorService,
    private val entitlementService: EntitlementService,
) {
    fun registerPremises(request: PremisesRequest, authUserData: AuthUserData): Mono<Actor> {
        return registerRoles(request, authUserData)
            .flatMap { role -> createReadPremisesEntitlement(request.premisesId, role).map { role } }
            .collectList()
            .flatMap { roles ->
                createInitialEntitlements(request.premisesId, roles).collectList().map { roles }
            }
            .flatMap { registerActor(authUserData, request, it) }
    }


    private fun registerActor(authUserData: AuthUserData, request: PremisesRequest, roles: List<Role>): Mono<Actor> {
        val defaultHumanActorRoles = listOf(RoleType.USER, RoleType.ADMIN, RoleType.OWNER)
        val humanRoles: List<RoleId> = roles.filter { defaultHumanActorRoles.contains(it.role) }.map { it.roleId }
        return actorService.registerActor(
            request = RegisterActorRequest(
                premisesId = request.premisesId,
                roles = humanRoles,
                identifier = Identifier(authUserData.userId, ActorIdentifier.USER),
            )
        )
    }

    private fun registerRoles(request: PremisesRequest, authUserData: AuthUserData): Flux<Role> {
        return createFlux(listOf(RoleType.GUEST, RoleType.USER, RoleType.ADMIN, RoleType.OWNER))
            .flatMap {
                roleService.createRole(RoleRequest(it.toString(), it), request.premisesId, authUserData)
            }
    }


    private fun createInitialEntitlements(premisesId: PremisesId, roles: List<Role>): Flux<Entitlement> {
        val adminAndOwnerRoles = roles.filter { role -> role.role == RoleType.ADMIN || role.role == RoleType.OWNER }
        return createFlux(adminAndOwnerRoles)
            .flatMap { role ->
                createFlux(getCreateEntitlementMap())
                    .flatMap {
                        val entitlementRequest = EntitlementRequest(
                            action = it.action,
                            resourceType = it.resourceType,
                            resourceId = if (it.resourceType == ResourceType.PREMISES) premisesId else "*",
                            roleId = role.roleId
                        )
                        entitlementService.createEntitlement(entitlementRequest, premisesId)
                    }
            }
    }


    private fun createReadPremisesEntitlement(premisesId: PremisesId, role: Role): Mono<Entitlement> {
        val entitlementRequest = EntitlementRequest(
            action = ActionType.READ,
            resourceType = ResourceType.PREMISES,
            resourceId = premisesId,
            roleId = role.roleId
        )
        return entitlementService.createEntitlement(entitlementRequest, premisesId)
    }

    fun registerDevice(request: RegisterDeviceRequest, invitationData: InvitationData): Mono<Actor> {
        return roleService.createRole(
            RoleRequest("Role_${request.deviceId}", RoleType.BOT),
            invitationData.premisesId,
            AuthUserData(invitationData.invitedBy)
        )
            .flatMap {
                val actorRequest = RegisterActorRequest(
                    premisesId = it.premisesId,
                    roles = listOf(it.roleId),
                    identifier = Identifier(request.deviceId, ActorIdentifier.DEVICE),
                )
                actorService.registerActor(actorRequest)
            }
    }

    private fun getCreateEntitlementMap(): List<ActionResource> {
        return listOf(
            ActionResource(ActionType.UPDATE, ResourceType.PREMISES),
            ActionResource(ActionType.DELETE, ResourceType.PREMISES),
            ActionResource(ActionType.CREATE, ResourceType.FEED),
            ActionResource(ActionType.CREATE, ResourceType.WIDGET),
            ActionResource(ActionType.CREATE, ResourceType.ZONE),
            ActionResource(ActionType.CREATE, ResourceType.DEVICE),
            ActionResource(ActionType.CREATE, ResourceType.AUTOMATION_TRIGGER),
            ActionResource(ActionType.CREATE, ResourceType.AUTOMATION_RULE),
            ActionResource(ActionType.CREATE, ResourceType.AUTOMATION_ACTION),
            ActionResource(ActionType.CREATE, ResourceType.AUTOMATION_CONDITION),
        )
    }
}

data class ActionResource(val action: ActionType, val resourceType: ResourceType)
