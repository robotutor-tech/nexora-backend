package com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config

import com.robotutor.nexora.modules.automation.domain.entity.config.ConfigType

data class AutomationConfigDocument(val automationId: String) : ConfigDocument(ConfigType.AUTOMATION)
