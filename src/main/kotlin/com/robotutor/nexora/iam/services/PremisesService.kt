package com.robotutor.nexora.iam.services

import com.robotutor.nexora.iam.controllers.view.PremisesRequest
import com.robotutor.nexora.iam.controllers.view.RegisterActorRequest
import com.robotutor.nexora.iam.controllers.view.RoleRequest
import com.robotutor.nexora.iam.models.Actor
import com.robotutor.nexora.iam.models.ActorType
import com.robotutor.nexora.iam.models.Role
import com.robotutor.nexora.iam.models.RoleType
import com.robotutor.nexora.security.createFlux
import com.robotutor.nexora.security.models.AuthUserData
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service("IAMPremisesService")
class PremisesService(private val roleService: RoleService, private val actorService: ActorService) {


    fun registerPremises(request: PremisesRequest, authUserData: AuthUserData): Flux<Actor> {
        // TODO: create global policies
        return registerRoles(request)
            .collectList()
            // TODO: Assign policies to respective roles
            .flatMapMany {
                registerActors(authUserData, request, it)
            }
    }

    private fun registerActors(authUserData: AuthUserData, request: PremisesRequest, roles: List<Role>): Flux<Actor> {
        val defaultHumanActorRoles = listOf(RoleType.USER, RoleType.ADMIN, RoleType.OWNER)
        return actorService.registerActor(
            RegisterActorRequest(
                premisesId = request.premisesId,
                roles = roles.filter { defaultHumanActorRoles.contains(it.role) }.map { it.roleId },
                identifier = authUserData.userId,
                type = ActorType.HUMAN
            )
        )
    }

    private fun registerRoles(request: PremisesRequest): Flux<Role> {
        return createFlux(listOf(RoleType.GUEST, RoleType.USER, RoleType.ADMIN, RoleType.OWNER))
            .flatMapSequential {
                roleService.createRole(RoleRequest(it.toString(), it), request.premisesId)
            }
    }
}
