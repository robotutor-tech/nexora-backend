package com.robotutor.nexora.modules.widget.services

import com.robotutor.nexora.shared.adapters.messaging.services.KafkaConsumer
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class WidgetsCreateSubscriber(
    private val widgetService: WidgetService,
    private val kafkaConsumer: KafkaConsumer,
) {
    @PostConstruct
    fun init() {
//        kafkaConsumer.consume(listOf("widgets.create"), WidgetsCreateRequest::class.java) {
//            Mono.deferContextual { ctx ->
//                val premisesActorData = ctx.get(PremisesActorData::class.java)
//                createFlux(it.message.widgets)
//                    .flatMap { widgetRequest -> widgetService.createWidget(widgetRequest, premisesActorData) }
//                    .collectList()
//            }
//        }
//            .subscribe()
    }

}


//data class WidgetsCreateRequest(val widgets: List<WidgetRequest>)
