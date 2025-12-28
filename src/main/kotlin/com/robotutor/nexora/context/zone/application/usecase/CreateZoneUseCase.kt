package com.robotutor.nexora.context.zone.application.usecase

import com.robotutor.nexora.context.zone.application.command.CreateZoneCommand
import com.robotutor.nexora.context.zone.application.policy.CreateZonePolicy
import com.robotutor.nexora.context.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.context.zone.domain.exception.ZoneError
import com.robotutor.nexora.context.zone.domain.repository.ZoneRepository
import com.robotutor.nexora.shared.domain.utility.errorOnDenied
import com.robotutor.nexora.shared.application.observability.AppLoggerFactory
import com.robotutor.nexora.shared.application.observability.logOnError
import com.robotutor.nexora.shared.application.observability.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CreateZoneUseCase(
    private val createZonePolicy: CreateZonePolicy,
    private val zoneRepository: ZoneRepository,
    loggerFactory: AppLoggerFactory,
) {
    private val logger = loggerFactory.forClass(this::class.java)

    fun execute(command: CreateZoneCommand): Mono<ZoneAggregate> {
        return createZonePolicy.evaluate(command)
            .errorOnDenied(ZoneError.NEXORA0201)
            .map { ZoneAggregate.createZone(command.premisesId, command.name, command.createdBy) }
            .flatMap { zone -> zoneRepository.save(zone) }
            .logOnSuccess(logger, "Successfully created zone")
            .logOnError(logger, "Failed to create zone")
    }
}