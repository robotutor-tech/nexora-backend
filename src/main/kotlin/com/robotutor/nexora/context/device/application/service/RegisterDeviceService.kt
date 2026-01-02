package com.robotutor.nexora.context.device.application.service

import com.robotutor.nexora.context.device.application.command.RegisterDeviceCommand
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.repository.DeviceRepository
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceType
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterDeviceService(
    private val deviceRepository: DeviceRepository,

    ) {
    private val logger = Logger(this::class.java)

    @Authorize(ActionType.CREATE, ResourceType.DEVICE)
    fun execute(command: RegisterDeviceCommand): Mono<DeviceAggregate> {
        val device = DeviceAggregate.register(command.premisesId, command.name, command.registeredBy, command.zoneId)
        return deviceRepository.save(device)
            .logOnSuccess(logger, "Successfully registered new Device", mapOf("name" to command.name))
            .logOnError(logger, "Failed to register new Device", mapOf("name" to command.name))
    }
}