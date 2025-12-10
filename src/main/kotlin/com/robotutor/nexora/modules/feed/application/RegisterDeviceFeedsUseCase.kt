package com.robotutor.nexora.modules.feed.application

import com.robotutor.nexora.common.security.createFlux
import com.robotutor.nexora.modules.feed.application.command.CreateDeviceFeedsCommand
import com.robotutor.nexora.modules.feed.domain.event.DeviceFeedsCreatedEvent
import com.robotutor.nexora.modules.feed.domain.event.FeedEvent
import com.robotutor.nexora.modules.seed.SeedData.getCreateFeedCommands
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterDeviceFeedsUseCase(
    private val feedUseCase: FeedUseCase,
    private val eventPublisher: EventPublisher<FeedEvent>
) {
    val logger = Logger(this::class.java)

    fun createDeviceFeeds(
        createDeviceFeedsCommand: CreateDeviceFeedsCommand,
        actorData: ActorData,
    ): Mono<DeviceFeedsCreatedEvent> {
        val createFeedCommands = getCreateFeedCommands()
        return createFlux(createFeedCommands)
            .flatMap { createFeedCommand ->
                feedUseCase.createFeed(
                    createFeedCommand,
                    actorData,
                    createDeviceFeedsCommand.zoneId
                )
            }
            .collectList()
            .map { feeds ->
                val feedList = createFeedCommands.map { createFeedCommand ->
                    feeds.find { createFeedCommand.name == it.name }!!
                }
                DeviceFeedsCreatedEvent(createDeviceFeedsCommand.deviceId, feedList.map { it.feedId })
            }
            .publishEvent(eventPublisher)
            .logOnSuccess(logger, "Successfully created device feeds for ${createDeviceFeedsCommand.deviceId}")
            .logOnError(logger,  "Failed to create device feeds for ${createDeviceFeedsCommand.deviceId}")
    }
}