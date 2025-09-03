package com.robotutor.nexora.modules.feed.interfaces.messaging

import com.robotutor.nexora.modules.feed.application.RegisterDeviceFeedsUseCase
import com.robotutor.nexora.modules.feed.domain.event.DeviceFeedsCreatedEvent
import com.robotutor.nexora.modules.feed.interfaces.messaging.mapper.FeedMapper
import com.robotutor.nexora.modules.feed.interfaces.messaging.message.DeviceCreatedEventMessage
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaController
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEvent
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEventListener
import reactor.core.publisher.Mono

@KafkaController
class FeedEventController(
    private val registerDeviceFeedsUseCase: RegisterDeviceFeedsUseCase
) {

    @Suppress("UNUSED")
    @KafkaEventListener(["device.device.created"])
    fun handleDeviceCreated(
        @KafkaEvent event: DeviceCreatedEventMessage,
        actorData: ActorData
    ): Mono<DeviceFeedsCreatedEvent> {
        val createDeviceFeedsCommand = FeedMapper.toCreateDeviceFeedsCommand(event)
        return registerDeviceFeedsUseCase.createDeviceFeeds(createDeviceFeedsCommand, actorData)
    }
}

