package com.robotutor.nexora.modules.feed.services

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class FeedCreateSubscriber(
) {
    @PostConstruct
    fun init() {
//        kafkaConsumer.consume(listOf("feeds.create"), Device::class.java) { kafkaTopicMessage ->
//            Mono.deferContextual { ctx ->
//                val premisesActorData = ctx.get(PremisesActorData::class.java)
//                val device = kafkaTopicMessage.message
//                createFlux(device.feeds)
//                    .flatMap { feed ->
//                        feedService.createFeed(FeedRequest(feed.feed.name, feed.feed.type), premisesActorData)
//                    }
//                    .collectList()
//                    .flatMap { feeds ->
//                        val feedIds: List<FeedId> = device.feeds.map { deviceFeed ->
//                            feeds.find { it.name == deviceFeed.feed.name }!!.feedId
//                        }
//                        val message = mapOf("deviceId" to device.deviceId, "feeds" to feedIds)
//                        kafkaPublisher.publish("device.feeds.update", message) { feeds }
//                    }
//                    .flatMap { feeds ->
//                        val widgets = feeds.map { feed ->
//                            val widget = device.feeds.find { it.feed.name == feed.name }!!.widget
//                            mapOf(
//                                "feed" to feed.feedId,
//                                "name" to widget.name,
//                                "type" to widget.type,
//                                "zoneId" to widget.zoneId,
//                            )
//                        }
//                        kafkaPublisher.publish("widgets.create", mapOf("widgets" to widgets)) { feeds }
//                    }
//            }
//        }
//            .subscribe()
    }
}
