package com.robotutor.nexora.module.automation.infrastructure.persistence.mapper.config

import com.robotutor.nexora.module.automation.infrastructure.persistence.document.config.AutomationConfigDocument

object AutomationDocumentMapper: Mapper<AutomationConfig, AutomationConfigDocument> {
    override fun toDocument(config: AutomationConfig): AutomationConfigDocument {
        TODO("Not yet implemented")
    }

    override fun toDomain(doc: AutomationConfigDocument): AutomationConfig {
        TODO("Not yet implemented")
    }

}

