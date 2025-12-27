package com.robotutor.nexora.context.device.application.usecase

import com.robotutor.nexora.context.device.application.command.CompensateDeviceCommand
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.event.DeviceEventPublisher
import com.robotutor.nexora.context.device.domain.event.DeviceRegistrationCompensatedEvent
import com.robotutor.nexora.context.device.domain.repository.DeviceRepository
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CompensateDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val eventPublisher: DeviceEventPublisher
) {

    private val logger = Logger(this::class.java)

    @Authorize(ActionType.DELETE, ResourceType.DEVICE, "#command.deviceId")
    fun execute(command: CompensateDeviceCommand): Mono<DeviceAggregate> {
        return deviceRepository.deleteByDeviceId(command.deviceId)
            .publishEvent(eventPublisher, DeviceRegistrationCompensatedEvent(command.deviceId))
            .logOnSuccess(logger, "Successfully compensate device registration", mapOf("deviceId" to command.deviceId))
            .logOnError(logger, "Failed to compensate device registration", mapOf("deviceId" to command.deviceId))
    }

}
