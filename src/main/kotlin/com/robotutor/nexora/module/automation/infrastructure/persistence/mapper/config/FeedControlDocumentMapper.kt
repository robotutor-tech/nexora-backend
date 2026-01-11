package com.robotutor.nexora.module.automation.infrastructure.persistence.mapper.config

import com.robotutor.nexora.module.automation.domain.vo.component.FeedControl
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.config.FeedControlConfigDocument
import com.robotutor.nexora.shared.domain.vo.FeedId

object FeedControlDocumentMapper : Mapper<FeedControl, FeedControlConfigDocument> {
    override fun toDocument(config: FeedControl): FeedControlConfigDocument {
        return FeedControlConfigDocument(feedId = config.feedId.value, operator = config.operator, value = config.value)
    }

    override fun toDomain(doc: FeedControlConfigDocument): FeedControl {
        return FeedControl(feedId = FeedId(doc.feedId), operator = doc.operator, value = doc.value)
    }
}

