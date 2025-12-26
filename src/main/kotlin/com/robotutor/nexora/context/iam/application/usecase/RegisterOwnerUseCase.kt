package com.robotutor.nexora.context.iam.application.usecase

import com.robotutor.nexora.context.iam.application.command.RegisterGroupCommand
import com.robotutor.nexora.context.iam.application.command.RegisterOwnerCommand
import com.robotutor.nexora.context.iam.application.command.RegisterRoleCommand
import com.robotutor.nexora.context.iam.application.policy.RegisterPremisesOwnerPolicy
import com.robotutor.nexora.context.iam.application.seed.PermissionSeedProvider
import com.robotutor.nexora.context.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.context.iam.domain.aggregate.GroupType
import com.robotutor.nexora.context.iam.domain.aggregate.RoleAggregate
import com.robotutor.nexora.context.iam.domain.aggregate.RoleType
import com.robotutor.nexora.context.iam.domain.event.IAMEventPublisher
import com.robotutor.nexora.context.iam.domain.event.PremisesOwnerRegisteredEvent
import com.robotutor.nexora.context.iam.domain.event.PremisesOwnerRegistrationFailedEvent
import com.robotutor.nexora.context.iam.domain.exception.IAMError
import com.robotutor.nexora.context.iam.domain.repository.ActorRepository
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.domain.event.publishEventOnError
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.utility.errorOnDenied
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterOwnerUseCase(
    private val registerRoleUseCase: RegisterRoleUseCase,
    private val registerGroupUseCase: RegisterGroupUseCase,
    private val actorRepository: ActorRepository,
    private val permissionSeedProvider: PermissionSeedProvider,
    private val eventPublisher: IAMEventPublisher,
    private val registerPremisesOwnerPolicy: RegisterPremisesOwnerPolicy
) {
    fun execute(command: RegisterOwnerCommand): Mono<ActorAggregate> {
        return registerPremisesOwnerPolicy.evaluate(command)
            .errorOnDenied(IAMError.NEXORA0201)
            .flatMap {
                registerRoleUseCase.execute(createDefaultRoles(command)).collectList()
            }
            .flatMap { roles ->
                registerGroupUseCase.execute(createDefaultGroups(command, roles)).collectList()
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
            .flatMap { actorAggregate -> actorRepository.save(actorAggregate) }
            .publishEvent(eventPublisher, PremisesOwnerRegisteredEvent(command.premisesId))
            .publishEventOnError(eventPublisher, PremisesOwnerRegistrationFailedEvent(command.premisesId))
    }

    private fun createDefaultGroups(
        command: RegisterOwnerCommand,
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

    private fun createDefaultRoles(command: RegisterOwnerCommand): List<RegisterRoleCommand> {
        return listOf(
            RegisterRoleCommand(
                command.premisesId,
                Name("FULL_ACCESS"),
                RoleType.FULL_ACCESS,
                permissionSeedProvider.getDefaultPermissions(RoleType.FULL_ACCESS, command.premisesId)
            ),
            RegisterRoleCommand(
                command.premisesId,
                Name("FULL_READ"),
                RoleType.FULL_READ,
                permissionSeedProvider.getDefaultPermissions(RoleType.FULL_READ, command.premisesId)
            ),
            RegisterRoleCommand(
                command.premisesId,
                Name("FULL_WRITE"),
                RoleType.FULL_WRITE,
                permissionSeedProvider.getDefaultPermissions(RoleType.FULL_WRITE, command.premisesId)
            ),
            RegisterRoleCommand(
                command.premisesId,
                Name("READ_ONLY"),
                RoleType.READ_ONLY,
                permissionSeedProvider.getDefaultPermissions(RoleType.READ_ONLY, command.premisesId)
            ),
            RegisterRoleCommand(
                command.premisesId,
                Name("CONTROL_ONLY"),
                RoleType.CONTROL_ONLY,
                permissionSeedProvider.getDefaultPermissions(RoleType.CONTROL_ONLY, command.premisesId)
            ),
        )
    }
}