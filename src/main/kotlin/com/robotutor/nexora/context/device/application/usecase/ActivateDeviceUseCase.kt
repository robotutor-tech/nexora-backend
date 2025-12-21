package com.robotutor.nexora.context.device.application.usecase

import com.robotutor.nexora.context.device.application.command.ActivateDeviceCommand
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.application.policy.ActivateDevicePolicy
import com.robotutor.nexora.context.device.domain.repository.DeviceRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ActivateDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val activateDevicePolicy: ActivateDevicePolicy
) {
    fun execute(command: ActivateDeviceCommand): Mono<DeviceAggregate> {
        return deviceRepository.findByDeviceId(command.deviceId)
            .map { device -> device.activate(command.metaData) }
    }
}
