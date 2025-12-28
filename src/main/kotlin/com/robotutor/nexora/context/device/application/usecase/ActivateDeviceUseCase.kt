package com.robotutor.nexora.context.device.application.usecase

import com.robotutor.nexora.context.device.application.command.ActivateDeviceCommand
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.repository.DeviceRepository
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.application.observability.AppLoggerFactory
import com.robotutor.nexora.shared.application.observability.logOnError
import com.robotutor.nexora.shared.application.observability.logOnSuccess
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceType
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ActivateDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    loggerFactory: AppLoggerFactory,
) {
    private val logger = loggerFactory.forClass(this::class.java)

    @Authorize(ActionType.UPDATE, ResourceType.DEVICE, "#command.deviceId")
    fun execute(command: ActivateDeviceCommand): Mono<DeviceAggregate> {
        return deviceRepository.findByDeviceId(command.deviceId)
            .map { device -> device.activate() }
            .flatMap { device -> deviceRepository.save(device) }
            .logOnSuccess(logger, "Successfully activated device", mapOf("deviceId" to command.deviceId))
            .logOnError(logger, "Failed to activate device", mapOf("deviceId" to command.deviceId))
    }
}