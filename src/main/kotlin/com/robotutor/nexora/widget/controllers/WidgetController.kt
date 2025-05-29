package com.robotutor.nexora.widget.controllers

import com.robotutor.nexora.widget.controllers.view.WidgetRequest
import com.robotutor.nexora.widget.controllers.view.WidgetView
import com.robotutor.nexora.widget.services.WidgetService
import com.robotutor.nexora.security.models.PremisesActorData
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

    @PostMapping
    fun createWidget(
        @RequestBody @Validated widgetRequest: WidgetRequest,
        premisesActorData: PremisesActorData
    ): Mono<WidgetView> {
        return widgetService.createWidget(widgetRequest, premisesActorData).map { WidgetView.from(it) }
    }

    @PostMapping("/batch")
    fun createWidgets(
        @RequestBody @Validated widgetRequest: List<WidgetRequest>,
        premisesActorData: PremisesActorData
    ): Flux<WidgetView> {
        return widgetService.createWidgets(widgetRequest, premisesActorData).map { WidgetView.from(it) }
    }

    @GetMapping
    fun getWidgets(premisesActorData: PremisesActorData): Flux<WidgetView> {
        return widgetService.getWidgets(premisesActorData).map { WidgetView.from(it) }
    }
}