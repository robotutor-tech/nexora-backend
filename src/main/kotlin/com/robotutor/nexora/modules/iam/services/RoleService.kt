package com.robotutor.nexora.modules.iam.services

import com.robotutor.nexora.shared.logger.Logger
import org.springframework.stereotype.Service

@Service
class RoleService(
) {
    private val logger = Logger(this::class.java)

//    fun createRole(roleRequest: RoleRequest, premisesId: PremisesId, authUserData: AuthUserData): Mono<RoleDocument> {
//        return idGeneratorService.generateId(IdType.ROLE_ID)
//            .map { roleId -> RoleDocument.from(roleId, premisesId, roleRequest) }
//            .flatMap {
//                roleRepository.save(it)
//                    .auditOnSuccess(
//                        "ROLE_CREATED",
//                        mapOf("roleId" to it.roleId),
//                        identifier = Identifier(authUserData.userId, ActorIdentifier.USER),
//                        premisesId = premisesId
//                    )
//            }
//            .logOnSuccess(logger, "Successfully created new Role")
//            .logOnError(logger, "", "Failed to create new Role")
//    }
//
//    fun getRoleByRoleId(roleId: RoleId): Mono<RoleDocument> {
//        return roleRepository.findByRoleId(roleId)
//    }
//
//    fun getRolesByRoleIds(roleIds: List<RoleId>): Flux<RoleDocument> {
//        return roleRepository.findAllByRoleIdIn(roleIds)
//    }
//
//    fun getHumanRolesWithCurrent(premisesActorData: PremisesActorData): Flux<RoleDocument> {
//        val humanRoles = listOf(RoleType.USER, RoleType.ADMIN, RoleType.OWNER)
//        return roleRepository.findAllByPremisesIdAndRoleIn(premisesActorData.premisesId, humanRoles)
//            .concatWith(createMono(premisesActorData.roleDocument))
//            .distinct { it.roleId }
//    }
}
