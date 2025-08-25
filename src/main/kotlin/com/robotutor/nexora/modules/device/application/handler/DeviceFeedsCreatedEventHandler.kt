package com.robotutor.nexora.modules.device.application.handler

import com.robotutor.nexora.modules.device.application.DeviceUseCase
import com.robotutor.nexora.modules.device.domain.model.FeedIds
import com.robotutor.nexora.shared.domain.event.DeviceFeedsCreatedEvent
import com.robotutor.nexora.shared.domain.event.EventHandler
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.logger.Logger
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class DeviceFeedsCreatedEventHandler(
    private val deviceUseCase: DeviceUseCase
) : EventHandler<DeviceFeedsCreatedEvent>(DeviceFeedsCreatedEvent::class.java) {

    val logger = Logger(this::class.java)

    override fun handle(event: DeviceFeedsCreatedEvent, actorData: ActorData): Mono<Any> {
        return deviceUseCase.updateDeviceFeeds(event.deviceId, FeedIds(event.feedIds), actorData)
            .map { event }
    }
}