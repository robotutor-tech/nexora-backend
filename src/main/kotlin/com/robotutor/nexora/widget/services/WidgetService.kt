package com.robotutor.nexora.widget.services

import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.createFlux
import com.robotutor.nexora.widget.models.Widget
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.services.IdGeneratorService
import com.robotutor.nexora.widget.controllers.view.WidgetRequest
import com.robotutor.nexora.widget.models.IdType
import com.robotutor.nexora.widget.repositories.WidgetRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class WidgetService(
    private val idGeneratorService: IdGeneratorService,
    private val widgetRepository: WidgetRepository
) {
    val logger = Logger(this::class.java)

    fun createWidget(widgetRequest: WidgetRequest, premisesActorData: PremisesActorData): Mono<Widget> {
        return idGeneratorService.generateId(IdType.WIDGET_ID)
            .map { widgetId -> Widget.from(widgetId, widgetRequest, premisesActorData) }
            .flatMap { widget -> widgetRepository.save(widget) }
            .logOnSuccess(logger, "Successfully created new widget")
            .logOnError(logger, "", "Failed to create new widget")
    }

    fun getWidgets(premisesActorData: PremisesActorData): Flux<Widget> {
        return widgetRepository.findAllByPremisesIdAndFeedIdIn(
            premisesActorData.premisesId,
            premisesActorData.role.feeds
        )
    }

    fun createWidgets(widgetRequest: List<WidgetRequest>, premisesActorData: PremisesActorData): Flux<Widget> {
        return createFlux(widgetRequest).flatMapSequential { createWidget(it, premisesActorData) }
    }

}

