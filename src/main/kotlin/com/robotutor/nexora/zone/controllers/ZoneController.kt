package com.robotutor.nexora.zone.controllers

import com.robotutor.nexora.security.filters.annotations.ActionType
import com.robotutor.nexora.security.filters.annotations.RequireAccess
import com.robotutor.nexora.security.filters.annotations.ResourceType
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.models.ResourcesData
import com.robotutor.nexora.zone.controllers.view.ZoneCreateRequest
import com.robotutor.nexora.zone.controllers.view.ZoneView
import com.robotutor.nexora.zone.models.ZoneId
import com.robotutor.nexora.zone.services.ZoneService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/zones")
class ZoneController(private val zoneService: ZoneService) {

    @RequireAccess(ActionType.CREATE, ResourceType.ZONE)
    @PostMapping
    fun createZone(
        @RequestBody @Validated request: ZoneCreateRequest,
        premisesActorData: PremisesActorData
    ): Mono<ZoneView> {
        return zoneService.createZone(request, premisesActorData).map { ZoneView.from(it) }
    }

    @RequireAccess(ActionType.LIST, ResourceType.ZONE)
    @GetMapping
    fun getAllZones(premisesActorData: PremisesActorData, resourcesData: ResourcesData): Flux<ZoneView> {
        val zoneIds = resourcesData.getResourceIds(ActionType.READ, ResourceType.ZONE)
        return zoneService.getAllZones(premisesActorData, zoneIds).map { ZoneView.from(it) }
    }

    @RequireAccess(ActionType.READ, ResourceType.ZONE, "zoneId")
    @GetMapping("/{zoneId}")
    fun getZone(@PathVariable zoneId: ZoneId, premisesActorData: PremisesActorData): Mono<ZoneView> {
        return zoneService.getZone(zoneId, premisesActorData).map { ZoneView.from(it) }
    }
}