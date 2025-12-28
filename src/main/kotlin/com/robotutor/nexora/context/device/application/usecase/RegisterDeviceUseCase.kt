package com.robotutor.nexora.context.device.application.usecase

import com.robotutor.nexora.context.device.application.command.RegisterDeviceCommand
import com.robotutor.nexora.context.device.application.policy.RegisterDevicePolicy
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.exception.DeviceError
import com.robotutor.nexora.context.device.domain.repository.DeviceRepository
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.application.observability.AppLoggerFactory
import com.robotutor.nexora.shared.application.observability.logOnError
import com.robotutor.nexora.shared.application.observability.logOnSuccess
import com.robotutor.nexora.shared.domain.utility.errorOnDenied
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceType
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterDeviceUseCase(
    private val registerDevicePolicy: RegisterDevicePolicy,
    private val deviceRepository: DeviceRepository,
    loggerFactory: AppLoggerFactory,
) {
    private val logger = loggerFactory.forClass(this::class.java)

    @Authorize(ActionType.CREATE, ResourceType.DEVICE)
    fun execute(command: RegisterDeviceCommand): Mono<DeviceAggregate> {
        return registerDevicePolicy.evaluate(command)
            .errorOnDenied(DeviceError.NEXORA0401)
            .map {
                DeviceAggregate.register(
                    command.premisesId,
                    command.name,
                    command.registeredBy,
                    command.zoneId
                )
            }
            .flatMap { device -> deviceRepository.save(device) }
            .logOnSuccess(logger, "Successfully registered new Device", mapOf("name" to command.name))
            .logOnError(logger, "Failed to register new Device", mapOf("name" to command.name))
    }
}