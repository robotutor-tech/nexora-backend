package com.robotutor.nexora.modules.feed.infrastructure.persistence.mapper

import com.robotutor.nexora.modules.feed.domain.entity.Feed
import com.robotutor.nexora.modules.feed.infrastructure.persistence.document.FeedDocument
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper
import org.springframework.stereotype.Service

@Service
class FeedDocumentMapper : DocumentMapper<Feed, FeedDocument> {
    override fun toMongoDocument(domain: Feed): FeedDocument = FeedDocument(
        id = null,
        feedId = domain.feedId.value,
        premisesId = domain.premisesId.value,
        name = domain.name.value,
        value = domain.value,
        type = domain.type,
        createdAt = domain.createdAt,
        updatedAt = domain.updatedAt,
        version = domain.version
    )

    override fun toDomainModel(document: FeedDocument): Feed = Feed(
        feedId = FeedId(document.feedId),
        premisesId = PremisesId(document.premisesId),
        name = Name(document.name),
        value = document.value,
        type = document.type,
        createdAt = document.createdAt,
        updatedAt = document.updatedAt,
        version = document.version
    )
}

