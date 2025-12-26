package com.robotutor.nexora.context.zone.interfaces.controller

import com.robotutor.nexora.context.zone.application.usecase.CreateWidgetsUseCase
import com.robotutor.nexora.context.zone.application.usecase.CreateZoneUseCase
import com.robotutor.nexora.context.zone.application.usecase.ZoneUseCase
import com.robotutor.nexora.context.zone.interfaces.controller.view.ZoneRequest
import com.robotutor.nexora.context.zone.interfaces.controller.view.ZoneResponse
import com.robotutor.nexora.context.zone.interfaces.controller.mapper.ZoneMapper
import com.robotutor.nexora.context.zone.interfaces.controller.view.WidgetsRequest
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.application.annotation.ResourceId
import com.robotutor.nexora.shared.application.annotation.ResourceSelector
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.interfaces.view.AuthorizedResources
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/zones")
class ZoneController(
    private val createZoneUseCase: CreateZoneUseCase,
    private val zoneUseCase: ZoneUseCase,
    private val createWidgetsUseCase: CreateWidgetsUseCase
) {

    @Authorize(ActionType.CREATE, ResourceType.ZONE)
    @PostMapping
    fun createZone(@RequestBody @Validated request: ZoneRequest, actorData: ActorData): Mono<ZoneResponse> {
        val command = ZoneMapper.toCreateZoneCommand(request, actorData)
        return createZoneUseCase.execute(command)
            .map { ZoneMapper.toZoneResponse(it) }
    }

    @Authorize(ActionType.READ, ResourceType.ZONE)
    @GetMapping
    fun getAllZones(resources: AuthorizedResources): Flux<ZoneResponse> {
        val query = ZoneMapper.getZonesQuery(resources)
        return zoneUseCase.execute(query)
            .map { ZoneMapper.toZoneResponse(it) }
    }

    @Authorize(ActionType.READ, ResourceType.ZONE, ResourceSelector.SPECIFIC)
    @GetMapping("/{zoneId}")
    fun getZone(@PathVariable @ResourceId zoneId: String, actorData: ActorData): Mono<ZoneResponse> {
        val query = ZoneMapper.getZoneQuery(zoneId, actorData)
        return zoneUseCase.execute(query)
            .map { ZoneMapper.toZoneResponse(it) }
    }


    @Authorize(ActionType.CREATE, ResourceType.WIDGET)
    @PostMapping("/widgets")
    fun createWidgets(@RequestBody @Validated request: WidgetsRequest, actorData: ActorData): Mono<ZoneResponse> {
        val command = ZoneMapper.toCreateWidgetsCommand(request, actorData)
        return createWidgetsUseCase.execute(command)
            .map { ZoneMapper.toZoneResponse(it) }
    }

}