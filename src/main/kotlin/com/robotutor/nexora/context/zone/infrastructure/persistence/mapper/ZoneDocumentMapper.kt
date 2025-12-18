package com.robotutor.nexora.context.zone.infrastructure.persistence.mapper

import com.robotutor.nexora.context.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.shared.domain.vo.ZoneId
import com.robotutor.nexora.context.zone.infrastructure.persistence.document.ZoneDocument
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper

object ZoneDocumentMapper : DocumentMapper<ZoneAggregate, ZoneDocument> {

    override fun toDomainModel(document: ZoneDocument): ZoneAggregate {
        return ZoneAggregate.create(
            zoneId = ZoneId(document.zoneId),
            premisesId = PremisesId(document.premisesId),
            name = Name(document.name),
            createdBy = ActorId(document.createdBy),
            createdAt = document.createdAt,
            updatedAt = document.updatedAt
        ).setObjectIdAndVersion(document.id, document.version)
    }

    override fun toMongoDocument(domain: ZoneAggregate): ZoneDocument {
        return ZoneDocument(
            id = domain.getObjectId(),
            zoneId = domain.zoneId.value,
            name = domain.name.value,
            premisesId = domain.premisesId.value,
            createdBy = domain.createdBy.value,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            version = domain.getVersion(),
        )
    }
}