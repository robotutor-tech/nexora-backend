package com.robotutor.nexora.modules.widget.services

import com.robotutor.nexora.modules.iam.services.EntitlementResource
import com.robotutor.nexora.shared.adapters.messaging.auditOnSuccess
import com.robotutor.nexora.shared.adapters.messaging.services.KafkaPublisher
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import com.robotutor.nexora.common.security.application.annotations.ResourceType
import com.robotutor.nexora.common.security.models.PremisesActorData
import com.robotutor.nexora.common.security.service.IdGeneratorService
import com.robotutor.nexora.modules.widget.controllers.view.WidgetRequest
import com.robotutor.nexora.modules.widget.models.IdType
import com.robotutor.nexora.modules.widget.models.Widget
import com.robotutor.nexora.modules.widget.models.WidgetId
import com.robotutor.nexora.modules.widget.repositories.WidgetRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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
            .flatMap {
                val entitlementResource = EntitlementResource(ResourceType.WIDGET, it.widgetId)
                kafkaPublisher.publish("entitlement.create", entitlementResource) { it }
            }
            .flatMap { kafkaPublisher.publish("widget.create.success", it) { it } }
            .logOnSuccess(logger, "Successfully created new widget")
            .logOnError(logger, "", "Failed to create new widget")
    }

    fun getWidgets(premisesActorData: PremisesActorData, widgetIds: List<WidgetId>): Flux<Widget> {
        return widgetRepository.findAllByPremisesIdAndWidgetIdIn(premisesActorData.premisesId, widgetIds)
    }
}

