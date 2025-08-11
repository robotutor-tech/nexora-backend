package com.robotutor.nexora.modules.premises.controllers

import com.robotutor.nexora.modules.premises.controllers.view.PremisesCreateRequest
import com.robotutor.nexora.modules.premises.controllers.view.PremisesView
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.modules.premises.services.PremisesService
import com.robotutor.nexora.common.security.filters.annotations.*
import com.robotutor.nexora.common.security.models.AuthUserData
import com.robotutor.nexora.common.security.models.PremisesActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/premises")
class PremisesController(private val premisesService: PremisesService) {

    @PostMapping
    fun createPremises(
        @RequestBody @Validated premisesRequest: PremisesCreateRequest,
        authUserData: AuthUserData
    ): Mono<PremisesView> {
        return premisesService.createPremises(premisesRequest, authUserData).map { PremisesView.from(it) }
    }

    @GetMapping
    fun getPremises(@RequestParam premisesIds: List<PremisesId>): Flux<PremisesView> {
        return premisesService.getPremises(premisesIds).map { PremisesView.from(it) }
    }

    @RequireAccess(ActionType.READ, ResourceType.PREMISES, "premisesId")
    @GetMapping("/{premisesId}")
    fun getPremisesDetails(
        @PathVariable premisesId: PremisesId,
        premisesActorData: PremisesActorData
    ): Mono<PremisesView> {
        return premisesService.getPremisesDetails(premisesActorData).map { PremisesView.from(it) }
    }
}