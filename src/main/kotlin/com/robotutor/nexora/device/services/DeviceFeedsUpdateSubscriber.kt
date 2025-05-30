package com.robotutor.nexora.device.services

import com.robotutor.nexora.device.models.DeviceId
import com.robotutor.nexora.feed.models.FeedId
import com.robotutor.nexora.kafka.services.KafkaConsumer
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class DeviceFeedsUpdateSubscriber(
    private val deviceService: DeviceService,
    private val kafkaConsumer: KafkaConsumer
) {
    @PostConstruct
    fun init() {
        kafkaConsumer.consume(listOf("device.feeds.update"), DeviceFeedMap::class.java) {
            deviceService.updateFeed(it.message)
        }
            .subscribe()
    }

}


data class DeviceFeedMap(
    val deviceId: DeviceId,
    val feeds: List<FeedId>,
)