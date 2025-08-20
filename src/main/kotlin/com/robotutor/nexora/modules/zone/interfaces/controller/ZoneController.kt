package com.robotutor.nexora.modules.zone.interfaces.controller

import com.robotutor.nexora.common.security.application.annotations.ActionType
import com.robotutor.nexora.common.security.application.annotations.RequireAccess
import com.robotutor.nexora.common.security.application.annotations.ResourceType
import com.robotutor.nexora.common.security.models.PremisesActorData
import com.robotutor.nexora.common.security.models.ResourcesData
import com.robotutor.nexora.modules.zone.interfaces.controller.dto.ZoneCreateRequest
import com.robotutor.nexora.modules.zone.interfaces.controller.dto.ZoneView
import com.robotutor.nexora.modules.zone.application.ZoneUseCase
import com.robotutor.nexora.modules.zone.interfaces.controller.mapper.ZoneMapper
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ZoneId
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/zones")
class ZoneController(private val zoneUseCase: ZoneUseCase) {

    @RequireAccess(ActionType.CREATE, ResourceType.ZONE)
    @PostMapping
    fun createZone(
        @RequestBody @Validated request: ZoneCreateRequest,
        premisesActorData: PremisesActorData
    ): Mono<ZoneView> {
        val createZoneCommand = ZoneMapper.toCreateZoneCommand(request, premisesActorData)
        return zoneUseCase.createZone(createZoneCommand)
            .map { ZoneView.from(it) }
    }

    @RequireAccess(ActionType.LIST, ResourceType.ZONE)
    @GetMapping
    fun getAllZones(premisesActorData: PremisesActorData, resourcesData: ResourcesData): Flux<ZoneView> {
        val zoneIds = resourcesData.getResourceIds(ActionType.READ, ResourceType.ZONE)
        return zoneUseCase.getAllZones(PremisesId(premisesActorData.premisesId), zoneIds.map { ZoneId(it) })
            .map { ZoneView.from(it) }
    }

    @RequireAccess(ActionType.READ, ResourceType.ZONE, "zoneId")
    @GetMapping("/{zoneId}")
    fun getZone(@PathVariable zoneId: String, premisesActorData: PremisesActorData): Mono<ZoneView> {
        return zoneUseCase.getZone(ZoneId(zoneId), PremisesId(premisesActorData.premisesId))
            .map { ZoneView.from(it) }
    }
}