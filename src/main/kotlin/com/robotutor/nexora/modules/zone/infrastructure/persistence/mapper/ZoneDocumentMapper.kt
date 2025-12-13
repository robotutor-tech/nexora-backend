package com.robotutor.nexora.modules.zone.infrastructure.persistence.mapper

import com.robotutor.nexora.modules.zone.domain.entity.Zone
import com.robotutor.nexora.modules.zone.infrastructure.persistence.document.ZoneDocument
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.model.ZoneId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper

object ZoneDocumentMapper : DocumentMapper<Zone, ZoneDocument> {

    override fun toDomainModel(document: ZoneDocument): Zone {
        return Zone(
            zoneId = ZoneId(document.zoneId),
            premisesId = PremisesId(document.premisesId),
            name = Name(document.name),
            createdBy = ActorId(document.createdBy),
            createdAt = document.createdAt,
        ).setObjectIdAndVersion(document.id, document.version)
    }

    override fun toMongoDocument(domain: Zone): ZoneDocument {
        return ZoneDocument(
            id = domain.getObjectId(),
            zoneId = domain.zoneId.value,
            name = domain.name.value,
            premisesId = domain.premisesId.value,
            createdBy = domain.createdBy.value,
            createdAt = domain.createdAt,
            version = domain.getVersion(),
        )
    }
}