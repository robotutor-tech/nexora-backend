package com.robotutor.nexora.modules.automation.infrastructure.persistence.mapper

import com.robotutor.nexora.modules.automation.domain.entity.AutomationId
import com.robotutor.nexora.modules.automation.domain.entity.config.AutomationConfig
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.AutomationConfigDocument

object AutomationDocumentMapper {
    fun toDocument(config: AutomationConfig): AutomationConfigDocument {
        return AutomationConfigDocument(config.automationId.value)
    }
    fun toDomain(doc: AutomationConfigDocument): AutomationConfig {
        return AutomationConfig(automationId = AutomationId(doc.automationId))
    }
}

