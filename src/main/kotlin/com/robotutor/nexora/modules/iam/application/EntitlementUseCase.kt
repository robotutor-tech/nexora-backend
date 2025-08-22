package com.robotutor.nexora.modules.iam.application

import com.robotutor.nexora.modules.iam.application.command.CreateEntitlementCommand
import com.robotutor.nexora.modules.iam.domain.model.Entitlement
import com.robotutor.nexora.modules.iam.domain.model.EntitlementId
import com.robotutor.nexora.modules.iam.domain.model.IdType
import com.robotutor.nexora.modules.iam.domain.repository.EntitlementRepository
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.ResourceType
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class EntitlementUseCase(
    private val idGeneratorService: IdGeneratorService,
    private val entitlementRepository: EntitlementRepository
) {
    val logger = Logger(this::class.java)

    fun createEntitlement(createEntitlementCommand: CreateEntitlementCommand): Mono<Entitlement> {
        return idGeneratorService.generateId(IdType.ENTITLEMENT_ID)
            .map { entitlementId ->
                Entitlement(
                    entitlementId = EntitlementId(entitlementId),
                    roleId = createEntitlementCommand.roleId,
                    premisesId = createEntitlementCommand.premisesId,
                    action = createEntitlementCommand.action,
                    resourceType = createEntitlementCommand.resourceType,
                    resourceId = createEntitlementCommand.resourceId,
                )
            }
            .flatMap { entitlement -> entitlementRepository.save(entitlement) }
            .logOnSuccess(logger, "Successfully created new Entitlement")
            .logOnError(logger, "", "Failed to create new Entitlement")
    }

    fun getEntitlements(resourceType: ResourceType, action: ActionType, actorData: ActorData): Flux<Entitlement> {
        return entitlementRepository.findAllByPremisesIdAndRoleIdAndResourceTypeAndAction(
            actorData.premisesId,
            actorData.role.roleId,
            resourceType,
            action
        )
    }

}