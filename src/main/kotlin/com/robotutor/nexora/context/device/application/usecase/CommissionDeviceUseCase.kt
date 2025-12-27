package com.robotutor.nexora.context.device.application.usecase

import com.robotutor.nexora.context.device.application.command.CommissionDeviceCommand
import com.robotutor.nexora.context.device.application.facade.FeedFacade
import com.robotutor.nexora.context.device.application.facade.ZoneFacade
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.repository.DeviceRepository
import com.robotutor.nexora.context.device.domain.specification.DeviceByPremisesIdSpecification
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CommissionDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val zoneFacade: ZoneFacade,
    private val feedFacade: FeedFacade,
) {
    val logger = Logger(this::class.java)

    @Authorize(ActionType.UPDATE, ResourceType.DEVICE, "#command.deviceId")
    fun execute(command: CommissionDeviceCommand): Mono<DeviceAggregate> {
        val specification = DeviceByPremisesIdSpecification(command.actorData.premisesId)
        return deviceRepository.findBySpecification(specification)
            .flatMap { device ->
                feedFacade.registerFeeds(device.deviceId, command.metadata.modelNo)
                    .collectList()
                    .flatMap { feedIds ->
                        zoneFacade.registerWidgets(device.zoneId, command.metadata.modelNo, feedIds)
                            .map {
                                device
                                    .updateMetadata(command.metadata)
                                    .updateFeeds(feedIds.toSet())
                                    .commission(command.actorData.actorId)
                            }
                    }
            }
            .flatMap { device -> deviceRepository.save(device) }
            .logOnSuccess(logger, "Successfully updated device metadata")
            .logOnError(logger, "Failed to update device metadata")
    }
}