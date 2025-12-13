package com.robotutor.nexora.context.iam.application.usecase

import com.robotutor.nexora.context.iam.application.command.RegisterGroupCommand
import com.robotutor.nexora.context.iam.application.command.RegisterPremisesResourceCommand
import com.robotutor.nexora.context.iam.application.command.RegisterRoleCommand
import com.robotutor.nexora.context.iam.application.policy.RegisterPremisesResourcePolicy
import com.robotutor.nexora.context.iam.application.seed.PermissionSeedProvider
import com.robotutor.nexora.context.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.context.iam.domain.aggregate.GroupType
import com.robotutor.nexora.context.iam.domain.aggregate.RoleAggregate
import com.robotutor.nexora.context.iam.domain.aggregate.RoleType
import com.robotutor.nexora.context.iam.domain.exception.NexoraError
import com.robotutor.nexora.context.iam.domain.repository.ActorRepository
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.infrastructure.utility.errorOnDenied
import com.robotutor.nexora.shared.utility.createFlux
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterPremisesResourceUseCase(
    private val registerPremisesResourcePolicy: RegisterPremisesResourcePolicy,
    private val registerRoleUseCase: RegisterRoleUseCase,
    private val registerGroupUseCase: RegisterGroupUseCase,
    private val actorRepository: ActorRepository,
    private val permissionSeedProvider: PermissionSeedProvider
) {
    fun execute(command: RegisterPremisesResourceCommand): Mono<ActorAggregate> {
        return registerPremisesResourcePolicy.evaluate(command)
            .errorOnDenied(NexoraError.NEXORA0204)
            .flatMap {
                createFlux(createDefaultRoles(command))
                    .flatMap { registerGroupCommand -> registerRoleUseCase.execute(registerGroupCommand) }
                    .collectList()
            }
            .flatMap { roles ->
                createFlux(createDefaultGroups(command, roles))
                    .flatMap { registerGroupCommand -> registerGroupUseCase.execute(registerGroupCommand) }
                    .collectList()
                    .map { Pair(it, roles) }
            }
            .map { pair ->
                ActorAggregate.register(
                    accountId = command.owner.accountId,
                    premisesId = command.premisesId,
                    roleIds = pair.second.map { it.roleId },
                    groupIds = pair.first.map { it.groupId }
                )
            }
            .flatMap { actorAggregate -> actorRepository.save(actorAggregate).map { actorAggregate } }
    }

    private fun createDefaultGroups(
        command: RegisterPremisesResourceCommand,
        roles: List<RoleAggregate>
    ): List<RegisterGroupCommand> {
        return listOf(
            RegisterGroupCommand(
                premisesId = command.premisesId,
                name = Name("OWNER"),
                type = GroupType.OWNER,
                roleIds = roles.map { it.roleId }
            ),
            RegisterGroupCommand(
                premisesId = command.premisesId,
                name = Name("ADMIN"),
                type = GroupType.ADMIN,
                roleIds = roles.filter {
                    it.type in listOf(
                        RoleType.FULL_WRITE,
                        RoleType.FULL_READ,
                        RoleType.READ_ONLY,
                        RoleType.CONTROL_ONLY,
                    )
                }.map { it.roleId }
            ),
            RegisterGroupCommand(
                premisesId = command.premisesId,
                name = Name("USER"),
                type = GroupType.USER,
                roleIds = roles.filter {
                    it.type in listOf(
                        RoleType.FULL_READ,
                        RoleType.READ_ONLY,
                        RoleType.CONTROL_ONLY,
                    )
                }.map { it.roleId }
            ),
            RegisterGroupCommand(
                premisesId = command.premisesId,
                name = Name("GUEST"),
                type = GroupType.GUEST,
                roleIds = roles.filter { it.type in listOf(RoleType.READ_ONLY) }.map { it.roleId }
            )
        )
    }

    private fun createDefaultRoles(command: RegisterPremisesResourceCommand): List<RegisterRoleCommand> {
        return listOf(
            RegisterRoleCommand(
                command.premisesId,
                Name("FULL_ACCESS"),
                RoleType.FULL_ACCESS,
                permissionSeedProvider.getDefaultPermissions(RoleType.FULL_ACCESS)
            ),
            RegisterRoleCommand(
                command.premisesId,
                Name("FULL_READ"),
                RoleType.FULL_READ,
                permissionSeedProvider.getDefaultPermissions(RoleType.FULL_READ)
            ),
            RegisterRoleCommand(
                command.premisesId,
                Name("FULL_WRITE"),
                RoleType.FULL_WRITE,
                permissionSeedProvider.getDefaultPermissions(RoleType.FULL_WRITE)
            ),
            RegisterRoleCommand(
                command.premisesId,
                Name("READ_ONLY"),
                RoleType.READ_ONLY,
                permissionSeedProvider.getDefaultPermissions(RoleType.READ_ONLY)
            ),
            RegisterRoleCommand(
                command.premisesId,
                Name("CONTROL_ONLY"),
                RoleType.CONTROL_ONLY,
                permissionSeedProvider.getDefaultPermissions(RoleType.CONTROL_ONLY)
            ),
        )
    }
}