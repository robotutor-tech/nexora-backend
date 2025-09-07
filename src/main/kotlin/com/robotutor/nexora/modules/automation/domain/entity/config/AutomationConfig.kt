package com.robotutor.nexora.modules.automation.domain.entity.config

import com.robotutor.nexora.modules.automation.domain.entity.AutomationId

data class AutomationConfig(val automationId: AutomationId) : ActionConfig, RuleConfigType(ConfigType.AUTOMATION)
