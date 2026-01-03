package com.robotutor.nexora.module.zone.application.service

import com.robotutor.nexora.module.zone.application.command.GetZoneQuery
import com.robotutor.nexora.module.zone.application.command.GetZonesQuery
import com.robotutor.nexora.module.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.module.zone.domain.event.ZoneCreatedEvent
import com.robotutor.nexora.module.zone.domain.event.ZoneEventPublisher
import com.robotutor.nexora.module.zone.domain.repository.ZoneRepository
import com.robotutor.nexora.module.zone.domain.specification.ZoneByPremisesSpecification
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.specification.ResourceSpecificationBuilder
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.ZoneId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class GetZoneService(
    private val zoneRepository: ZoneRepository,
    private val resourceSpecificationBuilder: ResourceSpecificationBuilder<ZoneAggregate>,
    private val eventPublisher: ZoneEventPublisher,
) {

    fun execute(query: GetZonesQuery): Flux<ZoneAggregate> {
        val specification = resourceSpecificationBuilder.build(query.resources)
            .and(ZoneByPremisesSpecification(query.resources.premisesId))
        val event = ZoneCreatedEvent(
            ZoneId("zoneId"), Name("name"), query.resources.premisesId,
            createdBy = ActorId("actorId")
        )
        return eventPublisher.publish(event)
            .flatMapMany {
                zoneRepository.findAll(specification)
            }
    }

    fun execute(query: GetZoneQuery): Mono<ZoneAggregate> {
        return zoneRepository.findByZoneIdAndPremisesId(query.zoneId, query.premisesId)
    }

}