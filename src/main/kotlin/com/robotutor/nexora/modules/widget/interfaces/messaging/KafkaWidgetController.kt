package com.robotutor.nexora.modules.widget.interfaces.messaging

import com.robotutor.nexora.modules.widget.application.WidgetUseCase
import com.robotutor.nexora.modules.widget.domain.entity.Widget
import com.robotutor.nexora.modules.widget.interfaces.messaging.mapper.WidgetMapper
import com.robotutor.nexora.modules.widget.interfaces.messaging.message.CreateWidgetMessage
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaController
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEvent
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEventListener
import reactor.core.publisher.Mono

@KafkaController
class KafkaWidgetController(private val widgetUseCase: WidgetUseCase) {

    @Suppress("UNUSED")
    @KafkaEventListener(["feed.feed.created"])
    fun createWidgetOnFeedCreated(@KafkaEvent message: CreateWidgetMessage, actorData: ActorData): Mono<Widget> {
        return widgetUseCase.createWidget(WidgetMapper.toCreateWidgetCommand(message), actorData)
    }
}