package com.robotutor.nexora.feed.services

import com.robotutor.nexora.feed.controllers.view.FeedRequest
import com.robotutor.nexora.feed.models.FeedId
import com.robotutor.nexora.kafka.services.KafkaConsumer
import com.robotutor.nexora.kafka.services.KafkaPublisher
import com.robotutor.nexora.orchestration.models.Device
import com.robotutor.nexora.security.createFlux
import com.robotutor.nexora.security.models.PremisesActorData
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class FeedCreateSubscriber(
    private val feedService: FeedService,
    private val kafkaConsumer: KafkaConsumer,
    private val kafkaPublisher: KafkaPublisher
) {
    @PostConstruct
    fun init() {
        kafkaConsumer.consume(listOf("feed.create"), Device::class.java) { kafkaTopicMessage ->
            Mono.deferContextual { ctx ->
                val premisesActorData = ctx.get(PremisesActorData::class.java)
                val device = kafkaTopicMessage.message
                createFlux(device.feeds)
                    .flatMap { feed ->
                        feedService.createFeed(FeedRequest(feed.feed.name, feed.feed.type), premisesActorData)
                    }
                    .collectList()
                    .flatMap { feeds ->
                        val feedIds: List<FeedId> = device.feeds.map { deviceFeed ->
                            feeds.find { it.name == deviceFeed.feed.name }!!.feedId
                        }
                        val message = mapOf("deviceId" to device.deviceId, "feeds" to feedIds)
                        kafkaPublisher.publish("device.feeds.update", message) { feeds }
                    }
                    .flatMap { feeds ->
                        val widgets = feeds.map { feed ->
                            val widget = device.feeds.find { it.feed.name == feed.name }!!.widget
                            mapOf(
                                "feed" to feed.feedId,
                                "name" to widget.name,
                                "type" to widget.type,
                                "zoneId" to widget.zoneId,
                            )
                        }
                        kafkaPublisher.publish("widgets.create", mapOf("widgets" to widgets)) { feeds }
                    }
                    .flatMap { feeds ->
                        val allPolicies = feeds.flatMap { feed ->
                            val policies = device.feeds.find { it.feed.name == feed.name }!!.policies
                            policies.map { policy ->
                                mapOf(
                                    "premisesId" to feed.premisesId,
                                    "name" to policy.name,
                                    "type" to "LOCAL",
                                    "feedId" to feed.feedId,
                                    "access" to policy.access,
                                )
                            }
                        }
                        kafkaPublisher.publish("policies.create", mapOf("policies" to allPolicies))
                    }
            }
        }
            .subscribe()
    }
}
