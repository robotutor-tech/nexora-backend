package com.robotutor.nexora.context.device.application.usecase

import com.robotutor.nexora.context.device.application.command.UpdateMetaDataCommand
import com.robotutor.nexora.context.device.application.policy.MetaDataPolicy
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.exception.DeviceError
import com.robotutor.nexora.context.device.domain.repository.DeviceRepository
import com.robotutor.nexora.shared.infrastructure.utility.errorOnDenied
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ValidateMetaDataUseCase(
    private val deviceRepository: DeviceRepository,
    private val metaDataPolicy: MetaDataPolicy
) {
    val logger = Logger(this::class.java)

    fun execute(command: UpdateMetaDataCommand): Mono<DeviceAggregate> {
        return metaDataPolicy.evaluate(command)
            .errorOnDenied(DeviceError.NEXORA0402)
            .flatMap { deviceRepository.findByAccountId(command.accountId) }
            .map { it.updateMetadata(command.metadata) }
            .flatMap { deviceRepository.save(it) }
            .logOnSuccess(logger, "Successfully updated device metadata")
            .logOnError(logger, "Failed to update device metadata")
    }
}