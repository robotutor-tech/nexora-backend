package com.robotutor.nexora.modules.widget.application

import com.robotutor.nexora.modules.widget.application.command.CreateWidgetCommand
import com.robotutor.nexora.modules.widget.domain.model.IdType
import com.robotutor.nexora.modules.widget.domain.model.Widget
import com.robotutor.nexora.modules.widget.domain.repository.WidgetRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.WidgetId
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class WidgetUseCase(
    private val widgetRepository: WidgetRepository,
    private val idGeneratorService: IdGeneratorService
) {

    fun getWidgets(actorData: ActorData, widgetIds: List<WidgetId>): Flux<Widget> {
        return widgetRepository.findAllByPremisesIdAndWidgetIdIn(actorData.premisesId, widgetIds)
    }

    fun createWidget(createWidgetCommand: CreateWidgetCommand, actorData: ActorData): Mono<Widget> {
        return idGeneratorService.generateId(IdType.WIDGET_ID, WidgetId::class.java)
            .map { widgetId -> Widget.create(widgetId, createWidgetCommand, actorData) }
            .flatMap { widget -> widgetRepository.save(widget) }
            .publishEvents()
    }
}