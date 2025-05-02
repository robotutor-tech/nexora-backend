package com.robotutor.nexora.zone.controllers

import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.zone.controllers.view.ZoneView
import com.robotutor.nexora.zone.controllers.view.ZoneCreateRequest
import com.robotutor.nexora.zone.models.ZoneId
import com.robotutor.nexora.zone.services.ZoneService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/zones")
class ZoneController(private val zoneService: ZoneService) {

    @PostMapping
    fun createZone(
        @RequestBody @Validated request: ZoneCreateRequest,
        premisesActorData: PremisesActorData
    ): Mono<ZoneView> {
        return zoneService.createZone(request, premisesActorData).map { ZoneView.from(it) }
    }

    @GetMapping
    fun getAllZones(premisesActorData: PremisesActorData): Flux<ZoneView> {
        return zoneService.getAllZones(premisesActorData).map { ZoneView.from(it) }
    }

    @GetMapping("/{zoneId}")
    fun getZone(@PathVariable zoneId: ZoneId, premisesActorData: PremisesActorData): Mono<ZoneView> {
        return zoneService.getZone(zoneId, premisesActorData).map { ZoneView.from(it) }
    }
}