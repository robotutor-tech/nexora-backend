package com.robotutor.nexora.iam.services

import com.robotutor.nexora.iam.controllers.view.EntitlementRequest
import com.robotutor.nexora.iam.controllers.view.PremisesRequest
import com.robotutor.nexora.iam.controllers.view.RegisterActorRequest
import com.robotutor.nexora.iam.controllers.view.RegisterDeviceRequest
import com.robotutor.nexora.iam.controllers.view.RoleEntitlementRequest
import com.robotutor.nexora.iam.controllers.view.RoleRequest
import com.robotutor.nexora.iam.models.Actor
import com.robotutor.nexora.iam.models.Entitlement
import com.robotutor.nexora.iam.models.Role
import com.robotutor.nexora.iam.models.RoleEntitlement
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.iam.models.RoleType
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.createFlux
import com.robotutor.nexora.security.filters.annotations.ActionType
import com.robotutor.nexora.security.filters.annotations.ResourceType
import com.robotutor.nexora.security.models.ActorIdentifier
import com.robotutor.nexora.security.models.AuthUserData
import com.robotutor.nexora.security.models.Identifier
import com.robotutor.nexora.security.models.InvitationData
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service("IAMPremisesService")
class PremisesService(
    private val roleService: RoleService,
    private val actorService: ActorService,
    private val entitlementService: EntitlementService,
    private val roleEntitlementService: RoleEntitlementService
) {
    fun registerPremises(request: PremisesRequest, authUserData: AuthUserData): Mono<Actor> {
        return registerRoles(request, authUserData)
            .collectList()
            .flatMap { roles ->
                createEntitlements(request.premisesId, roles)
                    .collectList()
                    .map { roles }
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

    private fun createEntitlements(premisesId: PremisesId, roles: List<Role>): Flux<RoleEntitlement> {
        return createFlux(getEntitlementMap())
            .flatMap {
                entitlementService.createEntitlement(EntitlementRequest(it.action, it.resourceType), premisesId)
            }
            .filter { it.action == ActionType.READ || it.action == ActionType.CREATE }
            .flatMap { entitlement -> createRoleEntitlement(roles, entitlement, premisesId) }
    }

    private fun createRoleEntitlement(
        roles: List<Role>,
        entitlement: Entitlement,
        premisesId: PremisesId
    ): Flux<RoleEntitlement> {
        return createFlux(roles)
            .filter { it.role == RoleType.OWNER || it.role == RoleType.ADMIN || it.role == RoleType.USER }
            .map { RoleEntitlementRequest("*", entitlement.entitlementId, it.roleId) }
            .flatMap {
                roleEntitlementService.createRoleEntitlement(it, premisesId)
            }
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

    private fun getEntitlementMap(): List<ActionResource> {
        return listOf(
            ActionResource(ActionType.READ, ResourceType.PREMISES),
            ActionResource(ActionType.UPDATE, ResourceType.PREMISES),
            ActionResource(ActionType.CREATE, ResourceType.FEED),
            ActionResource(ActionType.READ, ResourceType.FEED),
            ActionResource(ActionType.CONTROL, ResourceType.FEED),
            ActionResource(ActionType.UPDATE, ResourceType.FEED),
            ActionResource(ActionType.CREATE, ResourceType.WIDGET),
            ActionResource(ActionType.READ, ResourceType.WIDGET),
            ActionResource(ActionType.UPDATE, ResourceType.WIDGET),
            ActionResource(ActionType.CREATE, ResourceType.ZONE),
            ActionResource(ActionType.READ, ResourceType.ZONE),
            ActionResource(ActionType.UPDATE, ResourceType.ZONE),
            ActionResource(ActionType.CREATE, ResourceType.DEVICE),
            ActionResource(ActionType.READ, ResourceType.DEVICE),
            ActionResource(ActionType.UPDATE, ResourceType.DEVICE),
        )
    }
}

data class ActionResource(val action: ActionType, val resourceType: ResourceType)
