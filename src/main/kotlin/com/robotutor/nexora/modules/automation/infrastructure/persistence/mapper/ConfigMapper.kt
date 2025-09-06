package com.robotutor.nexora.modules.automation.infrastructure.persistence.mapper

import com.robotutor.nexora.modules.automation.domain.entity.config.FeedControlConfig
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.FeedControlConfigDocument
import com.robotutor.nexora.shared.domain.model.FeedId

object ConfigMapper {
    fun toFeedControlConfigDocument(config: FeedControlConfig): FeedControlConfigDocument {
        return FeedControlConfigDocument(feedId = config.feedId.value, operator = config.operator, value = config.value)
    }

    fun toFeedControlConfig(config: FeedControlConfigDocument): FeedControlConfig {
        return FeedControlConfig(
            feedId = FeedId(config.feedId),
            operator = config.operator,
            value = config.value
        )
    }

}