package com.robotutor.nexora.modules.automation.infrastructure.persistence.mapper.config

import com.robotutor.nexora.modules.automation.domain.entity.config.FeedControlConfig
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.FeedControlConfigDocument
import com.robotutor.nexora.shared.domain.vo.FeedId

object FeedControlDocumentMapper : Mapper<FeedControlConfig, FeedControlConfigDocument> {
    override fun toDocument(config: FeedControlConfig): FeedControlConfigDocument {
        return FeedControlConfigDocument(feedId = config.feedId.value, operator = config.operator, value = config.value)
    }

    override fun toDomain(doc: FeedControlConfigDocument): FeedControlConfig {
        return FeedControlConfig(feedId = FeedId(doc.feedId), operator = doc.operator, value = doc.value)
    }
}

