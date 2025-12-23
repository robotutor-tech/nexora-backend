package com.robotutor.nexora.context.device.application.usecase

import com.robotutor.nexora.context.device.application.command.CompensateDeviceRegistrationCommand
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.event.DeviceEventPublisher
import com.robotutor.nexora.context.device.domain.event.DeviceRegistrationCompensatedEvent
import com.robotutor.nexora.context.device.domain.repository.DeviceRepository
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CompensateDeviceRegistrationUseCase(
    private val deviceRepository: DeviceRepository,
    private val eventPublisher: DeviceEventPublisher
) {

    private val logger = Logger(this::class.java)

    fun execute(command: CompensateDeviceRegistrationCommand): Mono<DeviceAggregate> {
        return deviceRepository.deleteByDeviceId(command.deviceId)
            .publishEvent(eventPublisher, DeviceRegistrationCompensatedEvent(command.deviceId))
            .logOnSuccess(logger, "Successfully compensate device registration", mapOf("deviceId" to command.deviceId))
            .logOnError(logger, "Failed to compensate device registration", mapOf("deviceId" to command.deviceId))
    }

}
