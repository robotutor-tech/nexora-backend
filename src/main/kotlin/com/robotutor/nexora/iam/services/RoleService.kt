package com.robotutor.nexora.iam.services

import com.robotutor.nexora.iam.controllers.view.RoleRequest
import com.robotutor.nexora.iam.models.IdType
import com.robotutor.nexora.iam.models.Role
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.iam.repositories.RoleRepository
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.services.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RoleService(private val idGeneratorService: IdGeneratorService, private val roleRepository: RoleRepository) {
    private val logger = Logger(this::class.java)

    fun createRole(roleRequest: RoleRequest, premisesId: PremisesId): Mono<Role?> {
        return idGeneratorService.generateId(IdType.ROLE_ID)
            .map { roleId -> Role.from(roleId, premisesId, roleRequest) }
            .flatMap { roleRepository.save(it) }
            .logOnSuccess(logger, "Successfully created new Role")
            .logOnError(logger, "", "Failed to create new Role")
    }

    fun getRoleByRoleId(roleId: RoleId): Mono<Role> {
        return roleRepository.findByRoleId(roleId)
    }

}
