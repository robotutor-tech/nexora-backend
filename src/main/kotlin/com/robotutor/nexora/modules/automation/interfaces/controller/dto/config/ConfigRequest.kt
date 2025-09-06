package com.robotutor.nexora.modules.automation.interfaces.controller.dto.config

sealed interface ConfigRequest
sealed interface ActionConfigRequest : ConfigRequest
sealed interface ConditionConfigRequest : ConfigRequest
sealed interface TriggerConfigRequest : ConfigRequest