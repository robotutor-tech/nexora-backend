package com.robotutor.nexora.premises.controllers

import com.robotutor.nexora.premises.controllers.view.PremisesCreateRequest
import com.robotutor.nexora.premises.controllers.view.PremisesView
import com.robotutor.nexora.premises.services.PremisesService
import com.robotutor.nexora.security.models.UserData
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
        userData: UserData
    ): Mono<PremisesView> {
        return premisesService.createPremises(premisesRequest, userData).map { PremisesView.from(it) }
    }

    @GetMapping
    fun getPremises(userData: UserData): Flux<PremisesView> {
        return premisesService.getPremises(userData).map { PremisesView.from(it) }
    }
}