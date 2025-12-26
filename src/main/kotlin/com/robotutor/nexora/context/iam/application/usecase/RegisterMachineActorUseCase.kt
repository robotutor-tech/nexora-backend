package com.robotutor.nexora.context.iam.application.usecase

import com.robotutor.nexora.context.iam.application.command.RegisterMachineActorCommand
import com.robotutor.nexora.context.iam.application.command.RegisterRoleCommand
import com.robotutor.nexora.context.iam.application.policy.RegisterMachineActorPolicy
import com.robotutor.nexora.context.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.context.iam.domain.aggregate.RoleType
import com.robotutor.nexora.context.iam.domain.exception.IAMError
import com.robotutor.nexora.context.iam.domain.repository.ActorRepository
import com.robotutor.nexora.context.iam.domain.vo.Permission
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.domain.utility.errorOnDenied
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterMachineActorUseCase(
    private val registerRoleUseCase: RegisterRoleUseCase,
    private val actorRepository: ActorRepository,
    private val registerMachineActorPolicy: RegisterMachineActorPolicy,
) {
    fun execute(command: RegisterMachineActorCommand): Mono<ActorAggregate> {
        return registerMachineActorPolicy.evaluate(command)
            .errorOnDenied(IAMError.NEXORA0209)
            .flatMap { registerRoleUseCase.execute(createMachineRoleCommand(command)) }
            .map { role ->
                ActorAggregate.register(
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