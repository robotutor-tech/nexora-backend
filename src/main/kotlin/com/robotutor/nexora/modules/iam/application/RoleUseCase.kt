package com.robotutor.nexora.modules.iam.application

import com.robotutor.nexora.modules.iam.domain.model.IdType
import com.robotutor.nexora.modules.iam.application.command.CreateRoleCommand
import com.robotutor.nexora.modules.iam.domain.model.Role
import com.robotutor.nexora.modules.iam.domain.repository.RoleRepository
import com.robotutor.nexora.shared.domain.model.RoleId
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
        return idGeneratorService.generateId(IdType.ROLE_ID)
            .map { roleId ->
                Role(
                    roleId = RoleId(roleId),
                    premisesId = createRoleCommand.premisesId,
                    name = createRoleCommand.name,
                    roleType = createRoleCommand.role
                )
            }
            .flatMap { role -> roleRepository.save(role) }
            .logOnSuccess(logger, "Successfully created new Role")
            .logOnError(logger, "", "Failed to create new Role")
    }

    fun getRolesByRoleIds(roles: List<RoleId>): Flux<Role> {
        return roleRepository.findAllByRoleIdIn(roles)
    }

    fun getByRoleId(roleId: RoleId): Mono<Role> {
        return roleRepository.findByRoleId(roleId)
    }

}