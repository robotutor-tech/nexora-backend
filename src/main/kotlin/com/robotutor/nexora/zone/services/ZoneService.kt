package com.robotutor.nexora.zone.services

import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.services.IdGeneratorService
import com.robotutor.nexora.zone.controllers.view.ZoneCreateRequest
import com.robotutor.nexora.zone.models.IdType
import com.robotutor.nexora.zone.models.Zone
import com.robotutor.nexora.zone.models.ZoneId
import com.robotutor.nexora.zone.repositories.ZoneRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ZoneService(
    private val zoneRepository: ZoneRepository,
    private val idGeneratorService: IdGeneratorService
) {
    val logger = Logger(this::class.java)

    fun createZone(request: ZoneCreateRequest, actorData: PremisesActorData): Mono<Zone> {
        return idGeneratorService.generateId(IdType.ZONE_ID)
            .map { zoneId -> Zone.from(zoneId, request.name, actorData) }
            .flatMap { zone -> zoneRepository.save(zone) }
            .logOnSuccess(logger, "Successfully created zone")
            .logOnError(logger, "", "Failed to create zone")
    }

    fun getAllZones(actorData: PremisesActorData): Flux<Zone> {
        return zoneRepository.findAllByPremisesId(actorData.premisesId)
    }

    fun getZone(zoneId: ZoneId, premisesActorData: PremisesActorData): Mono<Zone> {
        return zoneRepository.findByZoneIdAndPremisesId(zoneId, premisesActorData.premisesId)
    }
}
