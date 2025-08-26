package com.robotutor.nexora.modules.zone.adapters.persistence.repository.document

import com.robotutor.nexora.modules.zone.adapters.persistence.mapper.ZoneDocumentMapper
import com.robotutor.nexora.modules.zone.adapters.persistence.model.ZoneDocument
import com.robotutor.nexora.modules.zone.domain.model.Zone
import com.robotutor.nexora.shared.adapters.persistence.repository.BaseDocumentRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Component

@Component
class ZoneDocumentRepository(mongoTemplate: ReactiveMongoTemplate, zoneDocumentMapper: ZoneDocumentMapper) :
    BaseDocumentRepository<Zone, ZoneDocument>(mongoTemplate, ZoneDocument::class.java, zoneDocumentMapper) {
}