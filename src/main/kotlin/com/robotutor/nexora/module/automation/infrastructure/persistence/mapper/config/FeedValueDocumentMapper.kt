package com.robotutor.nexora.module.automation.infrastructure.persistence.mapper.config

import com.robotutor.nexora.module.automation.domain.vo.component.FeedValue
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.config.FeedValueConfigDocument
import com.robotutor.nexora.shared.domain.vo.FeedId

object FeedValueDocumentMapper: Mapper<FeedValue, FeedValueConfigDocument> {
    override fun toDocument(config: FeedValue): FeedValueConfigDocument {
        return FeedValueConfigDocument(feedId = config.feedId.value, value = config.value)
    }

    override fun toDomain(doc: FeedValueConfigDocument): FeedValue {
        return FeedValue(feedId = FeedId(doc.feedId), value = doc.value)
    }
}

