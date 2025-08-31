package com.robotutor.nexora.modules.device.interfaces.messaging

import com.robotutor.nexora.modules.device.application.UpdateFeedsUseCase
import com.robotutor.nexora.modules.device.domain.model.Device
import com.robotutor.nexora.modules.device.interfaces.messaging.dto.UpdateFeedsDto
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaController
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEvent
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEventListener
import reactor.core.publisher.Mono

@com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaController
class FeedsUpdateListener(private val updateFeedsUseCase: UpdateFeedsUseCase) {

//    @Suppress("UNUSED")
//    @KafkaEventListener(["device.feeds.update"])
//    fun deviceFeedsUpdate(@KafkaEvent deviceFeedsUpdateEvent: UpdateFeedsDto): Mono<Device> {
//        return updateFeedsUseCase.updateFeeds(deviceFeedsUpdateEvent.deviceId, deviceFeedsUpdateEvent.feeds)
//    }
}