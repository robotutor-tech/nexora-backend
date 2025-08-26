package com.robotutor.nexora.modules.zone.adapters.persistence.mapper

import com.robotutor.nexora.modules.zone.adapters.persistence.model.ZoneDocument
import com.robotutor.nexora.modules.zone.domain.model.Zone
import com.robotutor.nexora.shared.adapters.persistence.mapper.DocumentMapper
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ZoneId
import org.springframework.stereotype.Service

@Service
class ZoneDocumentMapper : DocumentMapper<Zone, ZoneDocument> {

    override fun toDomainModel(document: ZoneDocument): Zone {
        return Zone(
            zoneId = ZoneId(document.zoneId),
            premisesId = PremisesId(document.premisesId),
            name = Name(document.name),
            createdBy = ActorId(document.createdBy),
            createdAt = document.createdAt,
            version = document.version
        )
    }

    override fun toMongoDocument(domain: Zone): ZoneDocument {
        return ZoneDocument(
            zoneId = domain.zoneId.value,
            name = domain.name.value,
            premisesId = domain.premisesId.value,
            createdBy = domain.createdBy.value,
            createdAt = domain.createdAt,
        )
    }
}