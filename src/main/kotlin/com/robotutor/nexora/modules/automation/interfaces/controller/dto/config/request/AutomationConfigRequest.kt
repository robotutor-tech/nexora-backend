package com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.request

import com.robotutor.nexora.modules.automation.domain.entity.config.ConfigType


data class AutomationConfigRequest(val automationId: String) : ConfigRequest(ConfigType.AUTOMATION)
