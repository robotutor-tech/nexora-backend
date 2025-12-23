package com.robotutor.nexora.context.device.application.usecase

import com.robotutor.nexora.context.device.application.command.CommissionDeviceCommand
import com.robotutor.nexora.context.device.application.command.RegisterFeedCommand
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.aggregate.FeedType
import com.robotutor.nexora.context.device.domain.repository.DeviceRepository
import com.robotutor.nexora.context.device.domain.specification.DeviceByAccountIdSpecification
import com.robotutor.nexora.context.device.domain.specification.DeviceByPremisesIdSpecification
import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.context.device.domain.vo.FeedMode
import com.robotutor.nexora.context.device.domain.vo.FeedValueRange
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CommissionDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val registerFeedUseCase: RegisterFeedUseCase,
) {
    val logger = Logger(this::class.java)

    fun execute(command: CommissionDeviceCommand): Mono<DeviceAggregate> {
        val specification =
            DeviceByPremisesIdSpecification(command.actorData.premisesId).and(DeviceByAccountIdSpecification(command.actorData.accountId))
        return deviceRepository.findBySpecification(specification)
            .flatMap { device ->
                registerFeedUseCase.execute(createRegisterFeedCommands(device.premisesId, device.deviceId))
                    .collectList()
                    .map { feeds ->
                        device
                            .updateFeeds(feeds.map { it.feedId }.toSet())
                            .updateMetadata(command.metadata)
                    }
            }
            .flatMap { deviceRepository.save(it) }
            .logOnSuccess(logger, "Successfully updated device metadata")
            .logOnError(logger, "Failed to update device metadata")
    }

    private fun createRegisterFeedCommands(premisesId: PremisesId, deviceId: DeviceId): List<RegisterFeedCommand> {
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