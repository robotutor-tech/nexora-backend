package com.robotutor.nexora.modules.iam.interfaces.controller

import com.robotutor.nexora.modules.iam.application.EntitlementUseCase
import com.robotutor.nexora.modules.iam.interfaces.controller.dto.EntitlementResponse
import com.robotutor.nexora.modules.iam.interfaces.controller.mapper.EntitlementMapper
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.ResourceType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/entitlements")
class EntitlementController(private val entitlementUseCase: EntitlementUseCase) {

    @GetMapping
    fun getEntitlements(
        @RequestParam resourceType: ResourceType,
        @RequestParam action: ActionType,
        actorData: ActorData
    ): Flux<EntitlementResponse> {
        return entitlementUseCase.getEntitlements(resourceType, action, actorData)
            .map { entitlement -> EntitlementMapper.toEntitlementResponse(entitlement) }
    }
}

