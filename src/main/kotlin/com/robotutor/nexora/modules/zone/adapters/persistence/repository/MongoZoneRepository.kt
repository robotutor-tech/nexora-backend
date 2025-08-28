package com.robotutor.nexora.modules.zone.adapters.persistence.repository

import com.robotutor.nexora.modules.zone.adapters.persistence.mapper.ZoneDocumentMapper
import com.robotutor.nexora.modules.zone.adapters.persistence.model.ZoneDocument
import com.robotutor.nexora.modules.zone.domain.model.Zone
import com.robotutor.nexora.modules.zone.domain.model.repository.ZoneRepository
import com.robotutor.nexora.shared.adapters.persistence.repository.MongoRepository
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ZoneId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MongoZoneRepository(mongoTemplate: ReactiveMongoTemplate) :
    MongoRepository<Zone, ZoneDocument>(mongoTemplate, ZoneDocument::class.java, ZoneDocumentMapper()), ZoneRepository {

    override fun save(zone: Zone): Mono<Zone> {
        val query = Query(Criteria.where("zoneId").`is`(zone.zoneId.value))
        return this.findAndReplace(query, zone)
    }

    override fun findAllByPremisesIdAndZoneIdIn(premisesId: PremisesId, zoneIds: List<ZoneId>): Flux<Zone> {
        val query = Query(
            Criteria.where("premisesId").`is`(premisesId.value).and("zoneId").`in`(zoneIds.map { it.value })
        )
        return this.findAll(query = query)
    }
}