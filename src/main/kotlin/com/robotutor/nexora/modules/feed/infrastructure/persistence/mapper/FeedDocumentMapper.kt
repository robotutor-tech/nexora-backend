package com.robotutor.nexora.modules.feed.infrastructure.persistence.mapper

import com.robotutor.nexora.modules.feed.domain.entity.Feed
import com.robotutor.nexora.modules.feed.infrastructure.persistence.document.FeedDocument
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper

object FeedDocumentMapper : DocumentMapper<Feed, FeedDocument> {
    override fun toMongoDocument(domain: Feed): FeedDocument = FeedDocument(
        id = domain.getObjectId(),
        feedId = domain.feedId.value,
        premisesId = domain.premisesId.value,
        name = domain.name.value,
        value = domain.value,
        type = domain.type,
        createdAt = domain.createdAt,
        updatedAt = domain.updatedAt,
        version = domain.getVersion()
    )

    override fun toDomainModel(document: FeedDocument): Feed = Feed(
        feedId = FeedId(document.feedId),
        premisesId = PremisesId(document.premisesId),
        name = Name(document.name),
        value = document.value,
        type = document.type,
        createdAt = document.createdAt,
        updatedAt = document.updatedAt,
    ).setObjectIdAndVersion(document.id, document.version)
}

