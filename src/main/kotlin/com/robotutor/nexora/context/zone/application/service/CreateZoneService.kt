package com.robotutor.nexora.context.zone.application.service

import com.robotutor.nexora.context.zone.application.command.CreateZoneCommand
import com.robotutor.nexora.context.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.context.zone.domain.exception.ZoneError
import com.robotutor.nexora.context.zone.domain.policy.CreateZonePolicy
import com.robotutor.nexora.context.zone.domain.policy.context.DuplicateZoneNameContext
import com.robotutor.nexora.context.zone.domain.repository.ZoneRepository
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.utility.enforcePolicy
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CreateZoneService(
    private val createZonePolicy: CreateZonePolicy,
    private val zoneRepository: ZoneRepository,

    ) {
    private val logger = Logger(this::class.java)

    fun execute(command: CreateZoneCommand): Mono<ZoneAggregate> {
        return zoneRepository.existsByPremisesIdAndName(command.premisesId, command.name)
            .enforcePolicy(createZonePolicy, { DuplicateZoneNameContext(it, command.name) }, ZoneError.NEXORA0201)
            .map { ZoneAggregate.createZone(command.premisesId, command.name, command.createdBy) }
            .flatMap { zone -> zoneRepository.save(zone) }
            .logOnSuccess(logger, "Successfully created zone")
            .logOnError(logger, "Failed to create zone")
    }
}