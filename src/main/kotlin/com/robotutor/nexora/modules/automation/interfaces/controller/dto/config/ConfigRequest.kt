package com.robotutor.nexora.modules.automation.interfaces.controller.dto.config


sealed class ConfigRequest(val type: String)
sealed interface ActionConfigRequest
sealed interface ConditionConfigRequest

