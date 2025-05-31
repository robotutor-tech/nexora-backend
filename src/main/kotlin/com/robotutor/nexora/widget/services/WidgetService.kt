package com.robotutor.nexora.widget.services

import com.robotutor.nexora.iam.models.Permission
import com.robotutor.nexora.kafka.auditOnSuccess
import com.robotutor.nexora.kafka.services.KafkaPublisher
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
import reactor.kafka.sender.KafkaSender

@Service
class WidgetService(
    private val idGeneratorService: IdGeneratorService,
    private val widgetRepository: WidgetRepository,
    private val kafkaPublisher: KafkaPublisher,
) {
    val logger = Logger(this::class.java)

    fun createWidget(widgetRequest: WidgetRequest, premisesActorData: PremisesActorData): Mono<Widget> {
        return idGeneratorService.generateId(IdType.WIDGET_ID)
            .map { widgetId -> Widget.from(widgetId, widgetRequest, premisesActorData) }
            .flatMap { widget ->
                widgetRepository.save(widget)
                    .auditOnSuccess("WIDGET_CREATE", mapOf("widgetId" to widget.widgetId, "name" to widget.name))
            }
            .flatMap { kafkaPublisher.publish("widget.create.success", it) { it } }
            .logOnSuccess(logger, "Successfully created new widget")
            .logOnError(logger, "", "Failed to create new widget")
    }

    fun getWidgets(premisesActorData: PremisesActorData): Flux<Widget> {
        val widgetIds = premisesActorData.role.policies
            .filter { it.permission == Permission.WIDGET_READ }
            .map { it.identifier!!.id }
        return widgetRepository.findAllByPremisesIdAndWidgetIdIn(premisesActorData.premisesId, widgetIds)
    }
}

