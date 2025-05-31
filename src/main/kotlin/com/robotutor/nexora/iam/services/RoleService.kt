package com.robotutor.nexora.iam.services

import com.robotutor.nexora.iam.controllers.view.RoleRequest
import com.robotutor.nexora.iam.models.IdType
import com.robotutor.nexora.iam.models.Policy
import com.robotutor.nexora.iam.models.Role
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.iam.models.RoleType
import com.robotutor.nexora.iam.repositories.RoleRepository
import com.robotutor.nexora.kafka.auditOnSuccess
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.ActorIdentifier
import com.robotutor.nexora.security.models.AuthUserData
import com.robotutor.nexora.security.models.Identifier
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.services.IdGeneratorService
import com.robotutor.nexora.utils.retryOptimisticLockingFailure
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class RoleService(private val idGeneratorService: IdGeneratorService, private val roleRepository: RoleRepository) {
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

    fun assignPolicyToHumanRole(policies: List<Policy>, premisesActorData: PremisesActorData): Flux<Role> {
        val humanRoles = listOf(RoleType.USER, RoleType.ADMIN, RoleType.OWNER)
        return roleRepository.findAllByPremisesIdAndRoleIn(premisesActorData.premisesId, humanRoles)
            .map { it.addPolicies(policies = policies) }
            .flatMap {
                roleRepository.save(it)
                    .retryOptimisticLockingFailure()
                    .auditOnSuccess(
                        "POLICY_ASSIGNED",
                        mapOf("policies" to it.policies, "roleId" to it.roleId, "role" to it.role)
                    )
            }
    }

    fun assignPoliciesToCurrentActor(policies: List<Policy>, premisesActorData: PremisesActorData): Mono<Role> {
        return roleRepository.findByRoleId(premisesActorData.role.roleId)
            .map { it.addPolicies(policies) }
            .flatMap {
                roleRepository.save(it)
                    .retryOptimisticLockingFailure()
                    .auditOnSuccess(
                        "POLICY_ASSIGNED",
                        mapOf("policies" to it.policies, "roleId" to it.roleId, "role" to it.role)
                    )
            }
    }
}
