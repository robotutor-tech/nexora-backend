package com.robotutor.nexora.context.feed.application.service

import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.context.device.domain.vo.ModelNo
import com.robotutor.nexora.context.feed.application.command.RegisterFeedCommand
import com.robotutor.nexora.context.feed.application.command.RegisterFeedsCommand
import com.robotutor.nexora.context.feed.domain.aggregate.FeedAggregate
import com.robotutor.nexora.context.feed.domain.aggregate.FeedType
import com.robotutor.nexora.context.feed.domain.repository.FeedRepository
import com.robotutor.nexora.context.feed.domain.vo.FeedMode
import com.robotutor.nexora.context.feed.domain.vo.FeedValueRange
import com.robotutor.nexora.shared.domain.vo.PremisesId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class RegisterFeedService(
    private val feedRepository: FeedRepository,
) {
    fun execute(command: RegisterFeedsCommand): Flux<FeedAggregate> {
        val feeds = createRegisterFeedCommands(command.modelNo, command.premisesId, command.deviceId)
            .map {
                FeedAggregate.register(
                    deviceId = it.deviceId,
                    type = it.type,
                    range = it.range,
                    premisesId = it.premisesId,
                )
            }
        return feedRepository.saveAll(feeds)
    }

    private fun createRegisterFeedCommands(
        modelNo: ModelNo,
        premisesId: PremisesId,
        deviceId: DeviceId
    ): List<RegisterFeedCommand> {
        return listOf(
            RegisterFeedCommand(
                premisesId = premisesId,
                deviceId = deviceId,
                type = FeedType.ACTUATOR,
                range = FeedValueRange(),
            ),
            RegisterFeedCommand(
                premisesId = premisesId,
                deviceId = deviceId,
                type = FeedType.ACTUATOR,
                range = FeedValueRange(),
            ),
            RegisterFeedCommand(
                premisesId = premisesId,
                deviceId = deviceId,
                type = FeedType.ACTUATOR,
                range = FeedValueRange(),
            ),
            RegisterFeedCommand(
                premisesId = premisesId,
                deviceId = deviceId,
                type = FeedType.ACTUATOR,
                range = FeedValueRange(),
            ),
            RegisterFeedCommand(
                premisesId = premisesId,
                deviceId = deviceId,
                type = FeedType.ACTUATOR,
                range = FeedValueRange(),
            ),
            RegisterFeedCommand(
                premisesId = premisesId,
                deviceId = deviceId,
                type = FeedType.ACTUATOR,
                range = FeedValueRange(),
            ),
            RegisterFeedCommand(
                premisesId = premisesId,
                deviceId = deviceId,
                type = FeedType.ACTUATOR,
                range = FeedValueRange(FeedMode.ANALOG, 0, 100),
            ),
            RegisterFeedCommand(
                premisesId = premisesId,
                deviceId = deviceId,
                type = FeedType.ACTUATOR,
                range = FeedValueRange(FeedMode.ANALOG, 0, 100),
            )
        )
    }
}
