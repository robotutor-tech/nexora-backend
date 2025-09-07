package com.robotutor.nexora.modules.automation.domain.entity.config

data class WaitConfig(val duration: Int) : ActionConfig, RuleConfigType(ConfigType.WAIT) {
    init {
        require(duration in 0..60) { "Duration must be between 0 and 60" }
    }
}
