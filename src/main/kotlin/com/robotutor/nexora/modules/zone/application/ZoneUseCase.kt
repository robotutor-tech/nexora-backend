package com.robotutor.nexora.modules.zone.application

import com.robotutor.nexora.modules.zone.application.command.CreateZoneCommand
import com.robotutor.nexora.modules.zone.domain.event.ZoneEvent
import com.robotutor.nexora.modules.zone.domain.entity.IdType
import com.robotutor.nexora.modules.zone.domain.entity.Zone
import com.robotutor.nexora.modules.zone.domain.repository.ZoneRepository
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.ResourceCreatedEvent
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.domain.vo.ResourceType
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
    private val eventPublisher: EventPublisher<ResourceCreatedEvent>,
    private val zoneEventPublisher: EventPublisher<ZoneEvent>
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
            .flatMap { zone ->
                val resourceCreatedEvent = ResourceCreatedEvent(ResourceType.ZONE, ResourceId(zone.zoneId.value))
                zoneRepository.save(zone).map { zone }
                    .publishEvent(eventPublisher, resourceCreatedEvent)
            }
            .publishEvents(zoneEventPublisher)
            .logOnSuccess(logger, "Successfully created zone")
            .logOnError(logger,  "Failed to create zone")
    }

    fun getAllZones(actorData: ActorData, zoneIds: List<ZoneId>): Flux<Zone> {
        return zoneRepository.findAllByPremisesIdAndZoneIdIn(actorData.premisesId, zoneIds)
    }

//    fun getZone(zoneId: ZoneId, actorData: ActorData): Mono<Zone> {
//        return zoneRepository.findByZoneIdAndPremisesId(zoneId, actorData.premisesId)
//    }
}
