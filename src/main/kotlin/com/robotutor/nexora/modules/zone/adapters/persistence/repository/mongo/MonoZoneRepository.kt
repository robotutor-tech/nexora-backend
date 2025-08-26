package com.robotutor.nexora.modules.zone.adapters.persistence.repository.mongo

import com.robotutor.nexora.modules.zone.adapters.persistence.repository.document.ZoneDocumentRepository
import com.robotutor.nexora.modules.zone.domain.model.Zone
import com.robotutor.nexora.modules.zone.domain.model.repository.ZoneRepository
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ZoneId
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class MonoZoneRepository(private val repository: ZoneDocumentRepository) : ZoneRepository {
    override fun save(zone: Zone): Mono<Zone> {
        val query = Query(Criteria.where("zoneId").`is`(zone.zoneId.value))
        return repository.findAndReplace(query, zone)
    }

    override fun findAllByPremisesIdAndZoneIdIn(premisesId: PremisesId, zoneIds: List<ZoneId>): Flux<Zone> {
        val query = Query(
            Criteria.where("premisesId").`is`(premisesId.value)
                .andOperator(Criteria.where("zoneId").`in`(zoneIds.map { it.value }))
        )
        return repository.findAll(query)
    }
}