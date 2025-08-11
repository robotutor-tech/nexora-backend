package com.robotutor.nexora.modules.zone.services

import com.robotutor.nexora.modules.iam.services.EntitlementResource
import com.robotutor.nexora.shared.adapters.messaging.auditOnSuccess
import com.robotutor.nexora.shared.adapters.messaging.services.KafkaPublisher
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import com.robotutor.nexora.common.security.filters.annotations.ResourceType
import com.robotutor.nexora.common.security.models.PremisesActorData
import com.robotutor.nexora.common.security.services.IdGeneratorService
import com.robotutor.nexora.modules.zone.controllers.view.ZoneCreateRequest
import com.robotutor.nexora.modules.zone.models.IdType
import com.robotutor.nexora.modules.zone.models.Zone
import com.robotutor.nexora.modules.zone.models.ZoneId
import com.robotutor.nexora.modules.zone.repositories.ZoneRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ZoneService(
    private val zoneRepository: ZoneRepository,
    private val idGeneratorService: IdGeneratorService,
    private val kafkaPublisher: KafkaPublisher
) {
    val logger = Logger(this::class.java)

    fun createZone(request: ZoneCreateRequest, actorData: PremisesActorData): Mono<Zone> {
        return idGeneratorService.generateId(IdType.ZONE_ID)
            .map { zoneId -> Zone.from(zoneId, request.name, actorData) }
            .flatMap { zone ->
                zoneRepository.save(zone)
                    .auditOnSuccess("ZONE_CREATED", mapOf("zoneId" to zone.zoneId, "name" to zone.name))
            }
            .flatMap { zone ->
                val entitlementResource = EntitlementResource(ResourceType.ZONE, zone.zoneId)
                kafkaPublisher.publish("entitlement.create", entitlementResource) { zone }
            }
            .logOnSuccess(logger, "Successfully created zone")
            .logOnError(logger, "", "Failed to create zone")
    }

    fun getAllZones(actorData: PremisesActorData, zoneIds: List<ZoneId>): Flux<Zone> {
        return zoneRepository.findAllByPremisesIdAndZoneIdIn(actorData.premisesId, zoneIds)
    }

    fun getZone(zoneId: ZoneId, premisesActorData: PremisesActorData): Mono<Zone> {
        return zoneRepository.findByZoneIdAndPremisesId(zoneId, premisesActorData.premisesId)
    }
}
