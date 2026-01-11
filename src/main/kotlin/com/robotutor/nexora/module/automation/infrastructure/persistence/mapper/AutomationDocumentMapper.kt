package com.robotutor.nexora.module.automation.infrastructure.persistence.mapper

import com.robotutor.nexora.module.automation.domain.entity.AutomationId
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.config.AutomationConfigDocument

object AutomationDocumentMapper {
    fun toDocument(config: AutomationConfig): AutomationConfigDocument {
        return AutomationConfigDocument(config.automationId.value)
    }
    fun toDomain(doc: AutomationConfigDocument): AutomationConfig {
        return AutomationConfig(automationId = AutomationId(doc.automationId))
    }
}

