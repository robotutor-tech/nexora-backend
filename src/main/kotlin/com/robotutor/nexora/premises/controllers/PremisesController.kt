package com.robotutor.nexora.premises.controllers

import com.robotutor.nexora.premises.controllers.view.PremisesCreateRequest
import com.robotutor.nexora.premises.controllers.view.PremisesView
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.premises.services.PremisesService
import com.robotutor.nexora.security.models.AuthUserData
import com.robotutor.nexora.security.models.PremisesActorData
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

    @GetMapping("/details")
    fun getPremisesDetails(premisesActorData: PremisesActorData): Mono<PremisesView> {
        return premisesService.getPremisesDetails(premisesActorData).map { PremisesView.from(it) }
    }
}