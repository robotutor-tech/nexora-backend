package com.robotutor.nexora.context.device.application.usecase

import com.robotutor.nexora.context.device.application.command.CommissionDeviceCommand
import com.robotutor.nexora.context.device.application.facade.FeedFacade
import com.robotutor.nexora.context.device.application.facade.ZoneFacade
import com.robotutor.nexora.context.device.application.policy.CommissionDevicePolicy
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.event.DeviceCommissionedEvent
import com.robotutor.nexora.context.device.domain.event.DeviceEventPublisher
import com.robotutor.nexora.context.device.domain.exception.DeviceError
import com.robotutor.nexora.context.device.domain.repository.DeviceRepository
import com.robotutor.nexora.context.device.domain.specification.DeviceByDeviceIdSpecification
import com.robotutor.nexora.context.device.domain.specification.DeviceByPremisesIdSpecification
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.domain.utility.errorOnDenied
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceType
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CommissionDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val zoneFacade: ZoneFacade,
    private val feedFacade: FeedFacade,
    private val eventPublisher: DeviceEventPublisher,
    private val commissionDevicePolicy: CommissionDevicePolicy,

    ) {
    private val logger = Logger(this::class.java)

    @Authorize(ActionType.UPDATE, ResourceType.DEVICE, "#command.deviceId")
    fun execute(command: CommissionDeviceCommand): Mono<DeviceAggregate> {
        val specification = DeviceByPremisesIdSpecification(command.actorData.premisesId)
            .and(DeviceByDeviceIdSpecification(command.deviceId))
        return commissionDevicePolicy.evaluate(command)
            .errorOnDenied(DeviceError.NEXORA0402)
            .flatMap { deviceRepository.findBySpecification(specification) }
            .flatMap { device ->
                feedFacade.registerFeeds(device.deviceId, command.metadata.modelNo)
                    .collectList()
                    .flatMap { feedIds ->
                        zoneFacade.registerWidgets(device.zoneId, command.metadata.modelNo, feedIds)
                            .map {
                                device
                                    .commission(command.metadata, feedIds.toSet())
                            }
                    }
            }
            .flatMap { device -> deviceRepository.save(device) }
            .publishEvent(
                eventPublisher,
                DeviceCommissionedEvent(command.deviceId, command.actorData.actorId, command.actorData.premisesId)
            )
            .logOnSuccess(logger, "Successfully updated device metadata")
            .logOnError(logger, "Failed to update device metadata")
    }
}