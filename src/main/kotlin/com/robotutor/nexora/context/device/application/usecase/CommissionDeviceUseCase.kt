package com.robotutor.nexora.context.device.application.usecase

import com.robotutor.nexora.context.device.application.command.CommissionDeviceCommand
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.repository.DeviceRepository
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CommissionDeviceUseCase(
    private val deviceRepository: DeviceRepository
) {
    private val logger = Logger(this::class.java)

    fun execute(command: CommissionDeviceCommand): Mono<DeviceAggregate> {
        return deviceRepository.findByDeviceId(command.deviceId)
            .map { device -> device.commission(command.accountId) }
            .flatMap { device -> deviceRepository.save(device) }
            .logOnSuccess(logger, "Successfully commissioned device", mapOf("deviceId" to command.deviceId))
            .logOnError(logger, "Failed to commission device", mapOf("deviceId" to command.deviceId))
    }
}