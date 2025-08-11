package com.robotutor.nexora.modules.device.adapters.inbound.messaging

import com.robotutor.nexora.shared.adapters.messaging.services.KafkaConsumer
import com.robotutor.nexora.modules.device.adapters.inbound.messaging.dto.UpdateFeedsDto
import com.robotutor.nexora.modules.device.adapters.inbound.messaging.mapper.DeviceMapper
import com.robotutor.nexora.modules.device.application.UpdateFeedsUseCase
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class FeedsUpdateListener(
    private val kafkaConsumer: KafkaConsumer,
    private val updateFeedsUseCase: UpdateFeedsUseCase
) {
    @PostConstruct
    fun init() {
        kafkaConsumer.consume(listOf("device.feeds.update"), UpdateFeedsDto::class.java) {
            val deviceId = DeviceMapper.toDeviceId(it.message)
            val feedIds = DeviceMapper.toFeedIds(it.message)
            updateFeedsUseCase.updateFeeds(deviceId, feedIds)
        }
            .subscribe()
    }

}