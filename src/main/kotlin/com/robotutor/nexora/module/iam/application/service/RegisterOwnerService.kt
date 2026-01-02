package com.robotutor.nexora.module.iam.application.service

import com.robotutor.nexora.module.iam.application.command.RegisterGroupCommand
import com.robotutor.nexora.module.iam.application.command.RegisterPremisesOwnerCommand
import com.robotutor.nexora.module.iam.application.command.RegisterRoleCommand
import com.robotutor.nexora.module.iam.domain.policy.RegisterPremisesOwnerPolicy
import com.robotutor.nexora.module.iam.application.seed.PermissionSeedProvider
import com.robotutor.nexora.module.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.module.iam.domain.aggregate.GroupType
import com.robotutor.nexora.module.iam.domain.aggregate.RoleAggregate
import com.robotutor.nexora.module.iam.domain.aggregate.RoleType
import com.robotutor.nexora.module.iam.domain.event.IAMEventPublisher
import com.robotutor.nexora.module.iam.domain.event.PremisesOwnerRegisteredEvent
import com.robotutor.nexora.module.iam.domain.event.PremisesOwnerRegistrationFailedEvent
import com.robotutor.nexora.module.iam.domain.exception.IAMError
import com.robotutor.nexora.module.iam.domain.repository.ActorRepository
import com.robotutor.nexora.module.iam.domain.specification.ActorByPremisesIdSpecification
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.domain.event.publishEventOnError
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.utility.enforcePolicy
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterOwnerService(
    private val registerRoleService: RegisterRoleService,
    private val registerGroupService: RegisterGroupService,
    private val actorRepository: ActorRepository,
    private val permissionSeedProvider: PermissionSeedProvider,
    private val eventPublisher: IAMEventPublisher,
    private val registerPremisesOwnerPolicy: RegisterPremisesOwnerPolicy
) {
    fun execute(command: RegisterPremisesOwnerCommand): Mono<ActorAggregate> {
        return actorRepository.exitsBySpecification(ActorByPremisesIdSpecification(command.premisesId))
            .enforcePolicy(registerPremisesOwnerPolicy, { it }, IAMError.NEXORA0201)
            .flatMap {
                registerRoleService.execute(createDefaultRoles(command)).collectList()
            }
            .flatMap { roles ->
                registerGroupService.execute(createDefaultGroups(command, roles)).collectList()
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
        command: RegisterPremisesOwnerCommand,
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

    private fun createDefaultRoles(command: RegisterPremisesOwnerCommand): List<RegisterRoleCommand> {
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