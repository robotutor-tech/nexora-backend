package com.robotutor.nexora.modules.automation.domain.entity.config

import com.robotutor.nexora.modules.automation.domain.entity.AutomationId
import com.robotutor.nexora.shared.domain.model.FeedId

data class AutomationConfig(val automationId: AutomationId) : ActionConfig

data class FeedValueConfig(val feedId: FeedId, val value: Int) : FeedConfig, ActionConfig {
    init {
        require(value in 0..100) { "Value must be between 0 and 100" }
    }
}

data class WaitConfig(val duration: Int) : ActionConfig {
    init {
        require(duration in 0..60) { "Duration must be between 0 and 60" }
    }
}
