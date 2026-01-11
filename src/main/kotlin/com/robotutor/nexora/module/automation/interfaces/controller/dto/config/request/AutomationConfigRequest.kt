package com.robotutor.nexora.module.automation.interfaces.controller.dto.config.request


data class AutomationConfigRequest(val automationId: String) : ConfigRequest(ConfigType.AUTOMATION)
