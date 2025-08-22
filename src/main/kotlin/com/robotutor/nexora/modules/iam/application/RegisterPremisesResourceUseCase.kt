package com.robotutor.nexora.modules.iam.application

import com.robotutor.nexora.common.security.createFlux
import com.robotutor.nexora.modules.iam.application.command.CreateActorCommand
import com.robotutor.nexora.modules.iam.application.command.CreateEntitlementCommand
import com.robotutor.nexora.modules.iam.application.command.CreateRoleCommand
import com.robotutor.nexora.modules.iam.application.command.RegisterPremisesResourceCommand
import com.robotutor.nexora.modules.iam.domain.model.Actor
import com.robotutor.nexora.modules.iam.domain.model.Entitlement
import com.robotutor.nexora.modules.iam.domain.model.Role
import com.robotutor.nexora.shared.domain.model.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class RegisterPremisesResourceUseCase(
    private val roleUseCase: RoleUseCase,
    private val entitlementUseCase: EntitlementUseCase,
    private val actorUseCase: ActorUseCase
) {
    fun registerPremises(registerPremisesResourceCommand: RegisterPremisesResourceCommand): Mono<Actor> {
        return createFlux(listOf(RoleType.GUEST, RoleType.USER, RoleType.ADMIN, RoleType.OWNER))
            .flatMap { roleType ->
                val createRoleCommand = CreateRoleCommand(
                    premisesId = registerPremisesResourceCommand.premisesId,
                    name = roleType.name,
                    role = roleType
                )
                roleUseCase.createRole(createRoleCommand)
            }
            .flatMap { role ->
                val createEntitlementCommand = CreateEntitlementCommand(
                    premisesId = role.premisesId,
                    roleId = role.roleId,
                    resourceId = role.premisesId.value,
                    resourceType = ResourceType.PREMISES,
                    action = ActionType.READ
                )
                entitlementUseCase.createEntitlement(createEntitlementCommand)
                    .map { role }
            }
            .collectList()
            .flatMap { roles ->
                createInitialEntitlements(roles, registerPremisesResourceCommand.premisesId)
                    .collectList().map { roles }
            }
            .flatMap { roles ->
                val createActorCommand = CreateActorCommand(
                    premisesId = registerPremisesResourceCommand.premisesId,
                    roles = roles.map { it.roleId },
                    principalType = ActorPrincipalType.USER,
                    principal = UserContext(registerPremisesResourceCommand.owner.userId),
                )
                actorUseCase.createActor(createActorCommand)
            }
    }

    fun createInitialEntitlements(roles: List<Role>, premisesId: PremisesId): Flux<Entitlement> {
        val adminAndOwnerRoles =
            roles.filter { role -> role.roleType == RoleType.ADMIN || role.roleType == RoleType.OWNER }
        return createFlux(adminAndOwnerRoles)
            .flatMap { role ->
                createFlux(getCreateEntitlementMap())
                    .flatMap {
                        val entitlementCommand = CreateEntitlementCommand(
                            action = it.first,
                            resourceType = it.second,
                            resourceId = if (it.second == ResourceType.PREMISES) premisesId.value else "*",
                            roleId = role.roleId,
                            premisesId = premisesId
                        )
                        entitlementUseCase.createEntitlement(entitlementCommand)
                    }
            }
    }

    private fun getCreateEntitlementMap(): List<Pair<ActionType, ResourceType>> {
        return listOf(
            Pair(ActionType.UPDATE, ResourceType.PREMISES),
            Pair(ActionType.DELETE, ResourceType.PREMISES),
            Pair(ActionType.CREATE, ResourceType.FEED),
            Pair(ActionType.CREATE, ResourceType.WIDGET),
            Pair(ActionType.CREATE, ResourceType.ZONE),
            Pair(ActionType.CREATE, ResourceType.DEVICE),
            Pair(ActionType.CREATE, ResourceType.AUTOMATION_TRIGGER),
            Pair(ActionType.CREATE, ResourceType.AUTOMATION_RULE),
            Pair(ActionType.CREATE, ResourceType.AUTOMATION_ACTION),
            Pair(ActionType.CREATE, ResourceType.AUTOMATION_CONDITION),
        )
    }
}