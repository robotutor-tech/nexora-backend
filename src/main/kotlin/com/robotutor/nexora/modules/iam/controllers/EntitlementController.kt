package com.robotutor.nexora.modules.iam.controllers

import com.robotutor.nexora.modules.iam.controllers.view.EntitlementView
import com.robotutor.nexora.modules.iam.services.EntitlementService
import com.robotutor.nexora.common.security.filters.annotations.ActionType
import com.robotutor.nexora.common.security.filters.annotations.ResourceType
import com.robotutor.nexora.common.security.models.PremisesActorData
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/iam/entitlements")
class EntitlementController(private val entitlementService: EntitlementService) {

    @GetMapping
    fun getEntitlements(
        @RequestParam resourceType: ResourceType,
        @RequestParam action: ActionType,
        premisesActorData: PremisesActorData
    ): Flux<EntitlementView> {
        return entitlementService.getEntitlements(resourceType, action, premisesActorData)
            .map { entitlement -> EntitlementView.from(entitlement) }
    }
}

