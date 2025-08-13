package com.robotutor.nexora.modules.zone.application

import com.robotutor.nexora.modules.zone.application.command.CreateZoneCommand
import com.robotutor.nexora.shared.adapters.messaging.auditOnSuccess
import com.robotutor.nexora.shared.adapters.messaging.services.KafkaPublisher
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import com.robotutor.nexora.modules.zone.domain.model.IdType
import com.robotutor.nexora.modules.zone.domain.model.Zone
import com.robotutor.nexora.modules.zone.domain.model.repository.ZoneRepository
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ZoneId
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ZoneUseCase(
    private val zoneRepository: ZoneRepository,
    private val idGeneratorService: IdGeneratorService,
    private val kafkaPublisher: KafkaPublisher
) {
    val logger = Logger(this::class.java)

    fun createZone(createZoneCommand: CreateZoneCommand): Mono<Zone> {
        return idGeneratorService.generateId(IdType.ZONE_ID)
            .map { zoneId ->
                Zone(
                    zoneId = ZoneId(value = zoneId),
                    premisesId = PremisesId(createZoneCommand.premisesId),
                    name = createZoneCommand.name,
                    createdBy = ActorId(createZoneCommand.createdBy),
                )
            }
            .flatMap { zone ->
                zoneRepository.save(zone)
                    .auditOnSuccess("ZONE_CREATED", mapOf("zoneId" to zone.zoneId, "name" to zone.name))
            }
//            .flatMap { zone ->
//                val entitlementResource = EntitlementResource(ResourceType.ZONE, zone.zoneId)
//                kafkaPublisher.publish("entitlement.create", entitlementResource) { zone }
//            }
            .logOnSuccess(logger, "Successfully created zone")
            .logOnError(logger, "", "Failed to create zone")
    }

    fun getAllZones(premisesId: PremisesId, zoneIds: List<ZoneId>): Flux<Zone> {
        return zoneRepository.findAllByPremisesIdAndZoneIdIn(premisesId, zoneIds)
    }

    fun getZone(zoneId: ZoneId, premisesId: PremisesId): Mono<Zone> {
        return zoneRepository.findByZoneIdAndPremisesId(zoneId, premisesId)
    }
}
