package com.robotutor.nexora.modules.automation.infrastructure.persistence.mapper.config

import com.robotutor.nexora.modules.automation.domain.entity.config.WaitConfig
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.WaitConfigDocument

object WaitDocumentMapper : Mapper<WaitConfig, WaitConfigDocument> {
    override fun toDocument(config: WaitConfig): WaitConfigDocument {
        return WaitConfigDocument(duration = config.duration)
    }

    override fun toDomain(doc: WaitConfigDocument): WaitConfig {
        return WaitConfig(duration = doc.duration)
    }
}

