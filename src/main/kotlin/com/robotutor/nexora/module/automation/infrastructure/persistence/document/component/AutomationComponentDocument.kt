package com.robotutor.nexora.module.automation.infrastructure.persistence.document.component

import com.robotutor.nexora.module.automation.domain.vo.component.ComponentType
import org.springframework.data.annotation.TypeAlias

@TypeAlias("AUTOMATION")
data class AutomationComponentDocument(val automationId: String) : ComponentDocument(ComponentType.AUTOMATION)
