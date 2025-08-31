package com.robotutor.nexora.modules.feed.interfaces.messaging

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.modules.feed.application.FeedUseCase
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaController
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEvent
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEventListener
import com.robotutor.nexora.modules.feed.interfaces.messaging.event.DeviceCreatedEvent
import com.robotutor.nexora.shared.domain.model.ActorData
import reactor.core.publisher.Mono

@KafkaController
class KafkaFeedController(private val feedUseCase: FeedUseCase) {

    @Suppress("UNUSED")
    @KafkaEventListener(["device.device.created"])
    fun handleDeviceCreated(@KafkaEvent event: DeviceCreatedEvent, actorData: ActorData): Mono<DeviceCreatedEvent> {
        println("recieved event as $event, $actorData-----------------------")
        return createMono(event)
    }
}

