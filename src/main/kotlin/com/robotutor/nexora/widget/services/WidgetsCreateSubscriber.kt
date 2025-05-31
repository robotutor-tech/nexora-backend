package com.robotutor.nexora.widget.services

import com.robotutor.nexora.kafka.services.KafkaConsumer
import com.robotutor.nexora.kafka.services.KafkaPublisher
import com.robotutor.nexora.security.createFlux
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.widget.controllers.view.WidgetRequest
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class WidgetsCreateSubscriber(
    private val widgetService: WidgetService,
    private val kafkaConsumer: KafkaConsumer,
) {
    @PostConstruct
    fun init() {
        kafkaConsumer.consume(listOf("widgets.create"), WidgetsCreateRequest::class.java) {
            Mono.deferContextual { ctx ->
                val premisesActorData = ctx.get(PremisesActorData::class.java)
                createFlux(it.message.widgets)
                    .flatMap { widgetRequest -> widgetService.createWidget(widgetRequest, premisesActorData) }
                    .collectList()
            }
        }
            .subscribe()
    }

}


data class WidgetsCreateRequest(val widgets: List<WidgetRequest>)
