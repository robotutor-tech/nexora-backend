package com.robotutor.nexora.context.device.application.usecase

import com.robotutor.nexora.context.device.application.command.UpdateMetaDataCommand
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.repository.DeviceRepository
import com.robotutor.nexora.context.device.domain.specification.DeviceByAccountIdSpecification
import com.robotutor.nexora.context.device.domain.specification.DeviceByPremisesIdSpecification
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UpdateMetaDataUseCase(
    private val deviceRepository: DeviceRepository,
) {
    val logger = Logger(this::class.java)

    fun execute(command: UpdateMetaDataCommand): Mono<DeviceAggregate> {
        val specification = DeviceByPremisesIdSpecification(command.actorData.premisesId)
            .and(DeviceByAccountIdSpecification(command.actorData.accountId))
        return deviceRepository.findBySpecification(specification)
            .map { it.updateMetadata(command.metadata) }
            .flatMap { deviceRepository.save(it) }
            .logOnSuccess(logger, "Successfully updated device metadata")
            .logOnError(logger, "Failed to update device metadata")
    }
}