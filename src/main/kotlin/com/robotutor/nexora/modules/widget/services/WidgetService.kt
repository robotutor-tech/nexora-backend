package com.robotutor.nexora.modules.widget.services

import org.springframework.stereotype.Service

@Service
class WidgetService(
) {
//    val logger = Logger(this::class.java)
//
//    fun createWidget(widgetRequest: WidgetRequest, premisesActorData: PremisesActorData): Mono<Widget> {
//        return idGeneratorService.generateId(IdType.WIDGET_ID)
//            .map { widgetId -> Widget.from(widgetId, widgetRequest, premisesActorData) }
//            .flatMap { widget ->
//                widgetRepository.save(widget)
//                    .auditOnSuccess("WIDGET_CREATE", mapOf("widgetId" to widget.widgetId, "name" to widget.name))
//            }
//            .flatMap {
//                val entitlementResource = EntitlementResource(ResourceType.WIDGET, it.widgetId)
//                kafkaPublisher.publish("entitlement.create", entitlementResource) { it }
//            }
//            .flatMap { kafkaPublisher.publish("widget.create.success", it) { it } }
//            .logOnSuccess(logger, "Successfully created new widget")
//            .logOnError(logger, "", "Failed to create new widget")
//    }


}

