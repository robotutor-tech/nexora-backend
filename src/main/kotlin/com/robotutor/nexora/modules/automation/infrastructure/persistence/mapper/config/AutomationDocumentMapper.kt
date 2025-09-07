package com.robotutor.nexora.modules.automation.infrastructure.persistence.mapper.config

import com.robotutor.nexora.modules.automation.domain.entity.config.AutomationConfig
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.AutomationConfigDocument

object AutomationDocumentMapper: Mapper<AutomationConfig, AutomationConfigDocument> {
    override fun toDocument(config: AutomationConfig): AutomationConfigDocument {
        TODO("Not yet implemented")
    }

    override fun toDomain(doc: AutomationConfigDocument): AutomationConfig {
        TODO("Not yet implemented")
    }

}

