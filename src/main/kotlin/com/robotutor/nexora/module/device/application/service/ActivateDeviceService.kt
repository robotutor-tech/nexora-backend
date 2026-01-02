package com.robotutor.nexora.module.device.application.service

import com.robotutor.nexora.module.device.application.command.ActorRegisteredDeviceCommand
import com.robotutor.nexora.module.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.module.device.domain.repository.DeviceRepository
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceType
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ActivateDeviceService(
    private val deviceRepository: DeviceRepository,
    
) {
    private val logger = Logger(this::class.java)

    @Authorize(ActionType.UPDATE, ResourceType.DEVICE, "#command.deviceId")
    fun execute(command: ActorRegisteredDeviceCommand): Mono<DeviceAggregate> {
        return deviceRepository.findByDeviceId(command.deviceId)
            .map { device -> device.actorRegistered() }
            .flatMap { device -> deviceRepository.save(device) }
            .logOnSuccess(logger, "Successfully activated device", mapOf("deviceId" to command.deviceId))
            .logOnError(logger, "Failed to activate device", mapOf("deviceId" to command.deviceId))
    }
}