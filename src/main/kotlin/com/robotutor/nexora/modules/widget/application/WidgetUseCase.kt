package com.robotutor.nexora.modules.widget.application

import com.robotutor.nexora.modules.widget.application.command.CreateWidgetCommand
import com.robotutor.nexora.modules.widget.domain.event.WidgetEvent
import com.robotutor.nexora.modules.widget.domain.entity.IdType
import com.robotutor.nexora.modules.widget.domain.entity.Widget
import com.robotutor.nexora.modules.widget.domain.entity.WidgetId
import com.robotutor.nexora.modules.widget.domain.repository.WidgetRepository
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.ResourceCreatedEvent
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class WidgetUseCase(
    private val widgetRepository: WidgetRepository,
    private val idGeneratorService: IdGeneratorService,
    private val widgetEventPublisher: EventPublisher<WidgetEvent>,
    private val eventPublisher: EventPublisher<ResourceCreatedEvent>
) {

    fun getWidgets(actorData: ActorData, widgetIds: List<WidgetId>): Flux<Widget> {
        return widgetRepository.findAllByPremisesIdAndWidgetIdIn(actorData.premisesId, widgetIds)
    }

    fun createWidget(createWidgetCommand: CreateWidgetCommand, actorData: ActorData): Mono<Widget> {
        return idGeneratorService.generateId(IdType.WIDGET_ID, WidgetId::class.java)
            .map { widgetId -> Widget.create(widgetId, createWidgetCommand, actorData) }
            .flatMap { widget ->
                val event = ResourceCreatedEvent(ResourceType.WIDGET, ResourceId(widget.widgetId.value))
                widgetRepository.save(widget).map { widget }
                    .publishEvent(eventPublisher, event)
            }
            .publishEvents(widgetEventPublisher)
    }
}