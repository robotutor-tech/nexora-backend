package com.robotutor.nexora.module.identity.application.service

import com.robotutor.nexora.module.identity.application.command.RegisterMachineActorCommand
import com.robotutor.nexora.module.identity.application.command.RegisterRoleCommand
import com.robotutor.nexora.module.identity.domain.policy.RegisterMachineActorPolicy
import com.robotutor.nexora.module.identity.domain.aggregate.Actor
import com.robotutor.nexora.module.identity.domain.aggregate.RoleType
import com.robotutor.nexora.module.identity.domain.exception.IdentityError
import com.robotutor.nexora.module.identity.domain.policy.context.RegisterMachineActorPolicyContext
import com.robotutor.nexora.module.identity.domain.repository.ActorRepository
import com.robotutor.nexora.module.identity.domain.specification.ActorByAccountIdSpecification
import com.robotutor.nexora.module.identity.domain.specification.ActorByPremisesIdSpecification
import com.robotutor.nexora.module.identity.domain.vo.Permission
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.domain.utility.enforcePolicy
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterMachineActorService(
    private val registerRoleService: RegisterRoleService,
    private val actorRepository: ActorRepository,
    private val registerMachineActorPolicy: RegisterMachineActorPolicy,
) {
    fun execute(command: RegisterMachineActorCommand): Mono<Actor> {
        val specification = ActorByAccountIdSpecification(command.owner.accountId)
            .and(ActorByPremisesIdSpecification(command.premisesId))
        return actorRepository.exitsBySpecification(specification)
            .enforcePolicy(registerMachineActorPolicy, IdentityError.NEXORA0209) {
                RegisterMachineActorPolicyContext(it, command.owner)
            }
            .flatMap { registerRoleService.execute(createMachineRoleCommand(command)) }
            .map { role ->
                Actor.register(
                    accountId = command.owner.accountId,
                    premisesId = command.premisesId,
                    roleIds = listOf(role.roleId),
                    groupIds = emptyList()
                )
            }
            .flatMap { actorAggregate -> actorRepository.save(actorAggregate) }
    }

    private fun createMachineRoleCommand(command: RegisterMachineActorCommand): RegisterRoleCommand {
        return RegisterRoleCommand(
            command.premisesId,
            Name("DEVICE_ACCESS"),
            RoleType.DEVICE_ACCESS,
            listOf(
                Permission(ActionType.READ, ResourceType.DEVICE, command.deviceId, command.premisesId),
                Permission(ActionType.UPDATE, ResourceType.DEVICE, command.deviceId, command.premisesId),
                Permission(ActionType.CREATE, ResourceType.FEED, ResourceId.ALL, command.premisesId),
                Permission(ActionType.CREATE, ResourceType.WIDGET, ResourceId.ALL, command.premisesId),
            ),
        )
    }
}
