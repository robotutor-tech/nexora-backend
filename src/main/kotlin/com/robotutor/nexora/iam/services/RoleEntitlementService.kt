package com.robotutor.nexora.iam.services

import com.robotutor.nexora.iam.controllers.view.RoleEntitlementRequest
import com.robotutor.nexora.iam.models.IdType
import com.robotutor.nexora.iam.models.RoleEntitlement
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.iam.repositories.RoleEntitlementRepository
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.services.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class RoleEntitlementService(
    private val idGeneratorService: IdGeneratorService,
    private val roleEntitlementRepository: RoleEntitlementRepository
) {
    private val logger = Logger(this::class.java)

    fun createRoleEntitlement(
        request: RoleEntitlementRequest,
        premisesId: PremisesId,
    ): Mono<RoleEntitlement> {
        return idGeneratorService.generateId(IdType.ROLE_ENTITLEMENT_ID)
            .map { roleEntitlementId -> RoleEntitlement.from(roleEntitlementId, premisesId, request) }
            .flatMap {
                roleEntitlementRepository.save(it)
            }
            .logOnSuccess(logger, "Successfully created new Role Entitlement")
            .logOnError(logger, "", "Failed to create new Role Entitlement")
    }

    fun getAllByRoleId(roleId: RoleId): Flux<RoleEntitlement> {
        return roleEntitlementRepository.findAllByRoleId(roleId)
    }
}
