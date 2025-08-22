package com.robotutor.nexora.modules.widget.interfaces.controller

import com.robotutor.nexora.common.security.application.annotations.RequireAccess
import com.robotutor.nexora.modules.widget.application.WidgetUseCase
import com.robotutor.nexora.modules.widget.interfaces.controller.dto.WidgetResponse
import com.robotutor.nexora.modules.widget.interfaces.controller.mapper.WidgetMapper
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.ResourceType
import com.robotutor.nexora.shared.domain.model.ResourcesData
import com.robotutor.nexora.shared.domain.model.WidgetId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/widgets")
class WidgetController(private val widgetUseCase: WidgetUseCase) {

    //    @RequireAccess(ActionType.CREATE, ResourceType.WIDGET)
//    @PostMapping
//    fun createWidget(
//        @RequestBody @Validated widgetRequest: WidgetRequest,
//        premisesActorData: PremisesActorData
//    ): Mono<WidgetView> {
//        return widgetService.createWidget(widgetRequest, premisesActorData).map { WidgetView.from(it) }
//    }
//
    @RequireAccess(ActionType.LIST, ResourceType.WIDGET)
    @GetMapping
    fun getWidgets(actorData: ActorData, resourcesData: ResourcesData): Flux<WidgetResponse> {
        val widgetIds = resourcesData.getResourceIds(ActionType.READ, ResourceType.WIDGET).map { WidgetId(it) }
        return widgetUseCase.getWidgets(actorData, widgetIds).map { WidgetMapper.toWidgetResponse(it) }
    }
}