package com.robotutor.nexora.context.device.application.usecase

import com.robotutor.nexora.context.device.application.command.RegisterDeviceCommand
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.exception.NexoraError
import com.robotutor.nexora.context.device.domain.policy.RegisterDevicePolicy
import com.robotutor.nexora.context.device.domain.repository.DeviceRepository
import com.robotutor.nexora.shared.infrastructure.utility.errorOnDenied
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterDeviceUseCase(
    private val registerDevicePolicy: RegisterDevicePolicy,
    private val deviceRepository: DeviceRepository
) {
    private val logger = Logger(this::class.java)

    fun execute(command: RegisterDeviceCommand): Mono<DeviceAggregate> {
        return registerDevicePolicy.evaluate(command)
            .errorOnDenied(NexoraError.NEXORA0401)
            .map { DeviceAggregate.register(command.premisesId, command.name, command.registeredBy, command.zoneId) }
            .flatMap { device -> deviceRepository.save(device) }
            .logOnSuccess(logger, "Successfully registered new Device", mapOf("name" to command.name))
            .logOnError(logger, "Failed to register new Device", mapOf("name" to command.name))
    }
}