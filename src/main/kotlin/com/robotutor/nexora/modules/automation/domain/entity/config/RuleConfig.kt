package com.robotutor.nexora.modules.automation.domain.entity.config

sealed interface Config {
    val type: ConfigType
}

sealed interface ActionConfig : Config
sealed interface TriggerConfig : Config
sealed interface ConditionConfig : Config

sealed class RuleConfigType(override val type: ConfigType) : Config