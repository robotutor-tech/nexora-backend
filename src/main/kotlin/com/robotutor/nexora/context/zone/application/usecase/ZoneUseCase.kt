package com.robotutor.nexora.context.zone.application.usecase

import com.robotutor.nexora.context.zone.application.command.GetZoneQuery
import com.robotutor.nexora.context.zone.application.command.GetZonesQuery
import com.robotutor.nexora.context.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.context.zone.domain.repository.ZoneRepository
import com.robotutor.nexora.context.zone.domain.specification.ZoneByPremisesSpecification
import com.robotutor.nexora.shared.domain.specification.AuthorizedQueryBuilder
import com.robotutor.nexora.shared.domain.vo.ZoneId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ZoneUseCase(
    private val zoneRepository: ZoneRepository,
    private val authorizedQueryBuilder: AuthorizedQueryBuilder<ZoneId, ZoneAggregate>,
    
) {

    fun execute(query: GetZonesQuery): Flux<ZoneAggregate> {
        val specification = authorizedQueryBuilder.build(query.resources)
            .and(ZoneByPremisesSpecification(query.resources.premisesId))
        return zoneRepository.findAll(specification)
    }

    fun execute(query: GetZoneQuery): Mono<ZoneAggregate> {
        return zoneRepository.findByZoneIdAndPremisesId(query.zoneId, query.premisesId)
    }

}