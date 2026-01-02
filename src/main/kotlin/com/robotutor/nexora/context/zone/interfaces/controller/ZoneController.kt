package com.robotutor.nexora.context.zone.interfaces.controller

import com.robotutor.nexora.common.security.domain.vo.AuthorizedResources
import com.robotutor.nexora.context.zone.application.service.CreateWidgetsService
import com.robotutor.nexora.context.zone.application.service.CreateZoneService
import com.robotutor.nexora.context.zone.application.service.ZoneService
import com.robotutor.nexora.context.zone.interfaces.controller.mapper.ZoneMapper
import com.robotutor.nexora.context.zone.interfaces.controller.view.WidgetsRequest
import com.robotutor.nexora.context.zone.interfaces.controller.view.ZoneRequest
import com.robotutor.nexora.context.zone.interfaces.controller.view.ZoneResponse
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/zones")
class ZoneController(
    private val createZoneService: CreateZoneService,
    private val zoneService: ZoneService,
    private val createWidgetsService: CreateWidgetsService
) {

    @PostMapping
    fun createZone(@RequestBody @Validated request: ZoneRequest, actorData: ActorData): Mono<ZoneResponse> {
        val command = ZoneMapper.toCreateZoneCommand(request, actorData)
        return createZoneService.execute(command)
            .map { ZoneMapper.toZoneResponse(it) }
    }

    @GetMapping
    fun getAllZones(resources: AuthorizedResources): Flux<ZoneResponse> {
        val query = ZoneMapper.getZonesQuery(resources)
        return zoneService.execute(query)
            .map { ZoneMapper.toZoneResponse(it) }
    }

    @GetMapping("/{zoneId}")
    fun getZone(@PathVariable zoneId: String, actorData: ActorData): Mono<ZoneResponse> {
        val query = ZoneMapper.getZoneQuery(zoneId, actorData)
        return zoneService.execute(query)
            .map { ZoneMapper.toZoneResponse(it) }
    }


    @PostMapping("/widgets")
    fun createWidgets(@RequestBody @Validated request: WidgetsRequest, actorData: ActorData): Mono<ZoneResponse> {
        val command = ZoneMapper.toCreateWidgetsCommand(request, actorData)
        return createWidgetsService.execute(command)
            .map { ZoneMapper.toZoneResponse(it) }
    }

}