package com.robotutor.nexora.modules.iam.services

import com.robotutor.nexora.modules.iam.controllers.view.RoleRequest
import com.robotutor.nexora.modules.iam.models.IdType
import com.robotutor.nexora.modules.iam.models.Role
import com.robotutor.nexora.modules.iam.models.RoleId
import com.robotutor.nexora.modules.iam.models.RoleType
import com.robotutor.nexora.modules.iam.repositories.RoleRepository
import com.robotutor.nexora.shared.adapters.messaging.auditOnSuccess
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.shared.domain.model.ActorIdentifier
import com.robotutor.nexora.common.security.models.AuthUserData
import com.robotutor.nexora.shared.domain.model.Identifier
import com.robotutor.nexora.common.security.models.PremisesActorData
import com.robotutor.nexora.common.security.service.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class RoleService(
    private val idGeneratorService: IdGeneratorService,
    private val roleRepository: RoleRepository,
) {
    private val logger = Logger(this::class.java)

    fun createRole(roleRequest: RoleRequest, premisesId: PremisesId, authUserData: AuthUserData): Mono<Role> {
        return idGeneratorService.generateId(IdType.ROLE_ID)
            .map { roleId -> Role.from(roleId, premisesId, roleRequest) }
            .flatMap {
                roleRepository.save(it)
                    .auditOnSuccess(
                        "ROLE_CREATED",
                        mapOf("roleId" to it.roleId),
                        identifier = Identifier(authUserData.userId, ActorIdentifier.USER),
                        premisesId = premisesId
                    )
            }
            .logOnSuccess(logger, "Successfully created new Role")
            .logOnError(logger, "", "Failed to create new Role")
    }

    fun getRoleByRoleId(roleId: RoleId): Mono<Role> {
        return roleRepository.findByRoleId(roleId)
    }

    fun getRolesByRoleIds(roleIds: List<RoleId>): Flux<Role> {
        return roleRepository.findAllByRoleIdIn(roleIds)
    }

    fun getHumanRolesWithCurrent(premisesActorData: PremisesActorData): Flux<Role> {
        val humanRoles = listOf(RoleType.USER, RoleType.ADMIN, RoleType.OWNER)
        return roleRepository.findAllByPremisesIdAndRoleIn(premisesActorData.premisesId, humanRoles)
            .concatWith(createMono(premisesActorData.role))
            .distinct { it.roleId }
    }
}
