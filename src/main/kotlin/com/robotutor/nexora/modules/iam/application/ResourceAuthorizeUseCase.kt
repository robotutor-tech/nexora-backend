package com.robotutor.nexora.modules.iam.application

import com.robotutor.nexora.modules.iam.application.command.AuthorizeResourceCommand
import com.robotutor.nexora.modules.iam.application.facade.OpaFacade
import com.robotutor.nexora.modules.iam.domain.entity.PolicyInput
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.ResourceContext
import com.robotutor.nexora.shared.domain.model.ResourceEntitlement
import com.robotutor.nexora.shared.logger.Logger
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ResourceAuthorizeUseCase(
    private val entitlementUseCase: EntitlementUseCase,
    private val opaFacade: OpaFacade
) {
    val logger = Logger(this::class.java)

    fun authorize(command: AuthorizeResourceCommand, actorData: ActorData): Mono<Boolean> {
        return entitlementUseCase.getEntitlements(command.resourceType, command.action, actorData)
            .map { entitlement ->
                val resource = ResourceContext(command.resourceType, command.resourceId, command.action)
                ResourceEntitlement(resource = resource, premisesId = entitlement.premisesId)
            }
            .collectList()
            .flatMap { entitlements ->
                val resource = ResourceContext(command.resourceType, command.resourceId, command.action)
                val input = PolicyInput(
                    resource = resource,
                    premisesId = actorData.premisesId,
                    entitlements = entitlements
                )
                opaFacade.evaluate(input)
            }
    }
}