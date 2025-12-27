package com.robotutor.nexora.context.device.application.usecase

import com.robotutor.nexora.context.device.application.command.ActivateDeviceCommand
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
class ActivateDeviceUseCase(private val deviceRepository: DeviceRepository) {
    val logger = Logger(this::class.java)

    @Authorize(ActionType.UPDATE, ResourceType.DEVICE, "#command.deviceId")
    fun execute(command: ActivateDeviceCommand): Mono<DeviceAggregate> {
        return deviceRepository.findByDeviceId(command.deviceId)
            .map { device -> device.activate() }
            .flatMap { device -> deviceRepository.save(device) }
            .logOnSuccess(logger, "Successfully activated device", mapOf("deviceId" to command.deviceId))
            .logOnError(logger, "Failed to activate device", mapOf("deviceId" to command.deviceId))
    }
}