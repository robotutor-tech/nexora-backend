package com.robotutor.nexora.modules.automation.infrastructure.persistence.mapper.config

import com.robotutor.nexora.modules.automation.domain.entity.config.FeedValueConfig
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.FeedValueConfigDocument
import com.robotutor.nexora.shared.domain.vo.FeedId

object FeedValueDocumentMapper: Mapper<FeedValueConfig, FeedValueConfigDocument> {
    override fun toDocument(config: FeedValueConfig): FeedValueConfigDocument {
        return FeedValueConfigDocument(feedId = config.feedId.value, value = config.value)
    }

    override fun toDomain(doc: FeedValueConfigDocument): FeedValueConfig {
        return FeedValueConfig(feedId = FeedId(doc.feedId), value = doc.value)
    }
}

