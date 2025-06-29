package com.robotutor.nexora.iam.services

import com.robotutor.nexora.iam.controllers.view.EntitlementRequest
import com.robotutor.nexora.iam.controllers.view.RoleRequest
import com.robotutor.nexora.iam.models.Entitlement
import com.robotutor.nexora.iam.models.EntitlementId
import com.robotutor.nexora.iam.models.IdType
import com.robotutor.nexora.iam.models.Role
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.iam.repositories.EntitlementRepository
import com.robotutor.nexora.iam.repositories.RoleRepository
import com.robotutor.nexora.kafka.auditOnSuccess
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.ActorIdentifier
import com.robotutor.nexora.security.models.AuthUserData
import com.robotutor.nexora.security.models.Identifier
import com.robotutor.nexora.security.services.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class EntitlementService(
    private val idGeneratorService: IdGeneratorService,
    private val entitlementRepository: EntitlementRepository
) {
    private val logger = Logger(this::class.java)

    fun createEntitlement(
        request: EntitlementRequest,
        premisesId: PremisesId,
    ): Mono<Entitlement> {
        return idGeneratorService.generateId(IdType.ENTITLEMENT_ID)
            .map { entitlementId -> Entitlement.from(entitlementId, premisesId, request) }
            .flatMap {
                entitlementRepository.save(it)
            }
            .logOnSuccess(logger, "Successfully created new Entitlement")
            .logOnError(logger, "", "Failed to create new Entitlement")
    }

    fun getByEntitlementId(entitlementId: EntitlementId): Mono<Entitlement> {
        return entitlementRepository.findByEntitlementId(entitlementId)
    }
}
