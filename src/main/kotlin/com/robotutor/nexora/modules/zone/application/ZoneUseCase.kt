package com.robotutor.nexora.modules.zone.application

import com.robotutor.nexora.modules.zone.application.command.CreateZoneCommand
import com.robotutor.nexora.modules.zone.domain.model.IdType
import com.robotutor.nexora.modules.zone.domain.model.Zone
import com.robotutor.nexora.modules.zone.domain.model.repository.ZoneRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.ZoneId
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ZoneUseCase(
    private val zoneRepository: ZoneRepository,
    private val idGeneratorService: IdGeneratorService,
) {
    val logger = Logger(this::class.java)

    fun createZone(createZoneCommand: CreateZoneCommand, actorData: ActorData): Mono<Zone> {
        return idGeneratorService.generateId(IdType.ZONE_ID, ZoneId::class.java)
            .map { zoneId ->
                Zone.create(
                    zoneId = zoneId,
                    premisesId = actorData.premisesId,
                    name = createZoneCommand.name,
                    createdBy = actorData.actorId,
                )
            }
            .flatMap { zone -> zoneRepository.save(zone) }
            .publishEvents()
            .logOnSuccess(logger, "Successfully created zone")
            .logOnError(logger, "", "Failed to create zone")
    }

    fun getAllZones(actorData: ActorData, zoneIds: List<ZoneId>): Flux<Zone> {
        return zoneRepository.findAllByPremisesIdAndZoneIdIn(actorData.premisesId, zoneIds)
    }

//    fun getZone(zoneId: ZoneId, actorData: ActorData): Mono<Zone> {
//        return zoneRepository.findByZoneIdAndPremisesId(zoneId, actorData.premisesId)
//    }
}
