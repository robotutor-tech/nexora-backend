package com.robotutor.nexora.modules.widget.controllers

import com.robotutor.nexora.common.security.application.annotations.ActionType
import com.robotutor.nexora.common.security.application.annotations.RequireAccess
import com.robotutor.nexora.common.security.application.annotations.ResourceType
import com.robotutor.nexora.modules.widget.controllers.view.WidgetRequest
import com.robotutor.nexora.modules.widget.controllers.view.WidgetView
import com.robotutor.nexora.modules.widget.services.WidgetService
import com.robotutor.nexora.common.security.models.PremisesActorData
import com.robotutor.nexora.common.security.models.ResourcesData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/widgets")
class WidgetController(private val widgetService: WidgetService) {

    @RequireAccess(ActionType.CREATE, ResourceType.WIDGET)
    @PostMapping
    fun createWidget(
        @RequestBody @Validated widgetRequest: WidgetRequest,
        premisesActorData: PremisesActorData
    ): Mono<WidgetView> {
        return widgetService.createWidget(widgetRequest, premisesActorData).map { WidgetView.from(it) }
    }

    @RequireAccess(ActionType.LIST, ResourceType.WIDGET)
    @GetMapping
    fun getWidgets(premisesActorData: PremisesActorData, resourcesData: ResourcesData): Flux<WidgetView> {
        val widgetIds = resourcesData.getResourceIds(ActionType.READ, ResourceType.WIDGET)
        return widgetService.getWidgets(premisesActorData, widgetIds).map { WidgetView.from(it) }
    }
}