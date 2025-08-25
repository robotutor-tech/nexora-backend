package com.robotutor.nexora.modules.feed.application.handler

import com.robotutor.nexora.common.security.createFlux
import com.robotutor.nexora.modules.feed.application.FeedUseCase
import com.robotutor.nexora.modules.seed.SeedData.getCreateFeedCommands
import com.robotutor.nexora.shared.domain.event.DeviceCreatedEvent
import com.robotutor.nexora.shared.domain.event.DeviceFeedsCreatedEvent
import com.robotutor.nexora.shared.domain.event.EventHandler
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.domain.model.ActorData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class DeviceCreatedEventHandler(private val feedUseCase: FeedUseCase) :
    EventHandler<DeviceCreatedEvent>(DeviceCreatedEvent::class.java) {
    override fun handle(event: DeviceCreatedEvent, actorData: ActorData): Mono<Any> {
        val createFeedCommands = getCreateFeedCommands(event.modelNo)
        return createFlux(createFeedCommands.toList())
            .flatMap { createFeedCommand -> feedUseCase.createFeed(createFeedCommand, actorData, event.zoneId) }
            .collectList()
            .map { feeds ->
                val feedList = createFeedCommands.map { createFeedCommand ->
                    feeds.find { createFeedCommand.name == it.name }!!
                }
                DeviceFeedsCreatedEvent(event.deviceId, feedList.map { it.feedId })
            }
            .publishEvent()
            .map { event }
    }
}