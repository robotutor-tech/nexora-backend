package com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.response

import com.robotutor.nexora.modules.automation.domain.entity.config.ConfigType


data class AutomationConfigResponse(
    val automationId: String
) : ConfigResponse(ConfigType.AUTOMATION)
