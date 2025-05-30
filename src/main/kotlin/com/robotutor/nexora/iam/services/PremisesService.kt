package com.robotutor.nexora.iam.services

import com.robotutor.nexora.iam.controllers.view.PremisesRequest
import com.robotutor.nexora.iam.controllers.view.RegisterActorRequest
import com.robotutor.nexora.iam.controllers.view.RegisterActorsRequest
import com.robotutor.nexora.iam.controllers.view.RegisterDeviceRequest
import com.robotutor.nexora.iam.controllers.view.RoleRequest
import com.robotutor.nexora.iam.models.Actor
import com.robotutor.nexora.iam.models.Role
import com.robotutor.nexora.iam.models.RoleType
import com.robotutor.nexora.security.createFlux
import com.robotutor.nexora.security.models.AuthUserData
import com.robotutor.nexora.security.models.InvitationData
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service("IAMPremisesService")
class PremisesService(private val roleService: RoleService, private val actorService: ActorService) {


    fun registerPremises(request: PremisesRequest, authUserData: AuthUserData): Flux<Actor> {
        // TODO: create global policies
        return registerRoles(request, authUserData)
            .collectList()
            // TODO: Assign policies to respective roles
            .flatMapMany {
                registerActors(authUserData, request, it)
            }
    }

    private fun registerActors(authUserData: AuthUserData, request: PremisesRequest, roles: List<Role>): Flux<Actor> {
        val defaultHumanActorRoles = listOf(RoleType.USER, RoleType.ADMIN, RoleType.OWNER)
        val humanRoles = roles.filter { defaultHumanActorRoles.contains(it.role) }.map { it.roleId }
        return actorService.registerActors(
            request = RegisterActorsRequest(premisesId = request.premisesId, roles = humanRoles),
            authUserData = authUserData
        )
    }

    private fun registerRoles(request: PremisesRequest, authUserData: AuthUserData): Flux<Role> {
        return createFlux(listOf(RoleType.GUEST, RoleType.USER, RoleType.ADMIN, RoleType.OWNER))
            .flatMapSequential {
                roleService.createRole(RoleRequest(it.toString(), it), request.premisesId, authUserData)
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
                    role = it.roleId,
                    identifier = request.deviceId,
                    type = request.type
                )
                actorService.registerActor(actorRequest, invitationData)
            }
    }
}
