package com.robotutor.nexora.modules.automation.domain.entity.config

import com.robotutor.nexora.shared.domain.model.FeedId

data class FeedValueConfig(val feedId: FeedId, val value: Int) : ActionConfig, RuleConfigType(ConfigType.FEED_VALUE) {
    init {
        require(value in 0..100) { "Value must be between 0 and 100" }
    }
}