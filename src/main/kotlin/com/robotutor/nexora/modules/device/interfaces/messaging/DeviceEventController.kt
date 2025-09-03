package com.robotutor.nexora.modules.device.interfaces.messaging

import com.robotutor.nexora.modules.device.application.UpdateDeviceFeedsUseCase
import com.robotutor.nexora.modules.device.domain.entity.Device
import com.robotutor.nexora.modules.device.interfaces.messaging.mapper.DeviceMapper
import com.robotutor.nexora.modules.device.interfaces.messaging.message.DeviceFeedsCreatedMessage
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaController
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEvent
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEventListener
import reactor.core.publisher.Mono

@KafkaController
class DeviceEventController(private val updateDeviceFeedsUseCase: UpdateDeviceFeedsUseCase) {

    @Suppress("UNUSED")
    @KafkaEventListener(["feed.device.feeds-created"])
    fun deviceFeedsUpdate(@KafkaEvent eventMessage: DeviceFeedsCreatedMessage): Mono<Device> {
        return updateDeviceFeedsUseCase.updateFeeds(
            DeviceMapper.toDeviceId(eventMessage),
            DeviceMapper.toFeedIds(eventMessage)
        )
    }
}