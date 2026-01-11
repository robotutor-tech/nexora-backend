package com.robotutor.nexora.module.automation.interfaces.controller.dto.config.response


data class AutomationConfigResponse(
    val automationId: String
) : ConfigResponse(ConfigType.AUTOMATION)
