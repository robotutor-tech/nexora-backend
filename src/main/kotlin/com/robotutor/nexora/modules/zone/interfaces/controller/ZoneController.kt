package com.robotutor.nexora.modules.zone.interfaces.controller

import com.robotutor.nexora.modules.zone.application.ZoneUseCase
import com.robotutor.nexora.modules.zone.interfaces.controller.dto.ZoneRequest
import com.robotutor.nexora.modules.zone.interfaces.controller.dto.ZoneResponse
import com.robotutor.nexora.modules.zone.interfaces.controller.mapper.ZoneMapper
import com.robotutor.nexora.shared.application.annotation.RequireAccess
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.ResourceType
import com.robotutor.nexora.shared.domain.model.ResourcesData
import com.robotutor.nexora.shared.domain.model.ZoneId
import com.robotutor.nexora.shared.domain.vo.ActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/zones")
class ZoneController(private val zoneUseCase: ZoneUseCase) {

    @RequireAccess(ActionType.WRITE, ResourceType.ZONE)
    @PostMapping
    fun createZone(@RequestBody @Validated request: ZoneRequest, actorData: ActorData): Mono<ZoneResponse> {
        val createZoneCommand = ZoneMapper.toCreateZoneCommand(request)
        return zoneUseCase.createZone(createZoneCommand, actorData)
            .map { ZoneMapper.toZoneResponse(it) }
    }

    @RequireAccess(ActionType.READ, ResourceType.ZONE)
    @GetMapping
    fun getAllZones(actorData: ActorData, resourcesData: ResourcesData): Flux<ZoneResponse> {
        val zoneIds = resourcesData.getResourceIds(ActionType.READ, ResourceType.ZONE).map { ZoneId(it) }
        return zoneUseCase.getAllZones(actorData, zoneIds)
            .map { ZoneMapper.toZoneResponse(it) }
    }

//    @RequireAccess(ActionType.READ, ResourceType.ZONE, "zoneId")
//    @GetMapping("/{zoneId}")
//    fun getZone(@PathVariable zoneId: String, actorData: ActorData): Mono<ZoneResponse> {
//        return zoneUseCase.getZone(ZoneId(zoneId), actorData)
//            .map { ZoneMapper.toZoneResponse(it) }
//    }
}