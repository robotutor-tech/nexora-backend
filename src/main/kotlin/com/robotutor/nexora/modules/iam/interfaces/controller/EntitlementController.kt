package com.robotutor.nexora.modules.iam.interfaces.controller

import com.robotutor.nexora.modules.iam.application.EntitlementUseCase
import com.robotutor.nexora.modules.iam.application.ResourceAuthorizeUseCase
import com.robotutor.nexora.modules.iam.application.command.AuthorizeResourceCommand
import com.robotutor.nexora.modules.iam.interfaces.controller.dto.EntitlementResponse
import com.robotutor.nexora.modules.iam.interfaces.controller.dto.ResourceRequest
import com.robotutor.nexora.modules.iam.interfaces.controller.mapper.EntitlementMapper
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.ResourceType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/entitlements")
class EntitlementController(
    private val entitlementUseCase: EntitlementUseCase,
    private val resourceAuthorizeUseCase: ResourceAuthorizeUseCase
) {

    @GetMapping
    fun getEntitlements(
        @RequestParam resourceType: ResourceType,
        @RequestParam action: ActionType,
        actorData: ActorData
    ): Flux<EntitlementResponse> {
        return entitlementUseCase.getEntitlements(resourceType, action, actorData)
            .map { entitlement -> EntitlementMapper.toEntitlementResponse(entitlement) }
    }

    @PostMapping("/authorize")
    fun authorize(@RequestBody @Validated resourceRequest: ResourceRequest, actorData: ActorData): Mono<Boolean> {
        val command = AuthorizeResourceCommand(
            resourceType = resourceRequest.resourceType,
            action = resourceRequest.action,
            resourceId = resourceRequest.resourceId
        )
        return resourceAuthorizeUseCase.authorize(command, actorData)
    }
}

