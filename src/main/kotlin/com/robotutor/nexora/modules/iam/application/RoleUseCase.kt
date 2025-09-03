package com.robotutor.nexora.modules.iam.application

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.modules.iam.application.command.CreateRoleCommand
import com.robotutor.nexora.modules.iam.domain.entity.IdType
import com.robotutor.nexora.modules.iam.domain.entity.Role
import com.robotutor.nexora.modules.iam.domain.repository.RoleRepository
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.domain.model.RoleType
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class RoleUseCase(
    private val idGeneratorService: IdGeneratorService,
    private val roleRepository: RoleRepository,
) {
    val logger = Logger(this::class.java)

    fun createRole(createRoleCommand: CreateRoleCommand): Mono<Role> {
        return idGeneratorService.generateId(IdType.ROLE_ID, RoleId::class.java)
            .map { roleId ->
                Role.create(
                    roleId = roleId,
                    premisesId = createRoleCommand.premisesId,
                    name = createRoleCommand.name,
                    roleType = createRoleCommand.roleType
                )
            }
            .flatMap { role -> roleRepository.save(role).map { role} }
            .logOnSuccess(logger, "Successfully created new Role")
            .logOnError(logger, "", "Failed to create new Role")
    }

    fun getRoles(premisesId: PremisesId, roleIds: List<RoleId>): Flux<Role> {
        return roleRepository.findAllByPremisesIdAndRoleIdIn(premisesId, roleIds)
    }

    fun getByRoleId(roleId: RoleId): Mono<Role> {
        return roleRepository.findByRoleId(roleId)
    }

    fun getHumanRolesWithCurrentRole(premisesId: PremisesId, actorData: ActorData): Flux<Role> {
        val roleTypes = listOf(RoleType.USER, RoleType.ADMIN, RoleType.OWNER)
        val role = Role(
            roleId = actorData.role.roleId,
            premisesId = actorData.role.premisesId,
            name = actorData.role.name,
            roleType = actorData.role.roleType
        )
        return roleRepository.findAllByPremisesIdAndRoleTypeIn(premisesId, roleTypes)
            .concatWith(createMono(role))
            .distinct { it.roleId }
    }

}