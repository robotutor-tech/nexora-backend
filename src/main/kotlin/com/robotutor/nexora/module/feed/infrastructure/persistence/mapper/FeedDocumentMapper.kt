package com.robotutor.nexora.module.feed.infrastructure.persistence.mapper

import com.robotutor.nexora.module.feed.domain.aggregate.FeedAggregate
import com.robotutor.nexora.module.feed.domain.vo.FeedValueRange
import com.robotutor.nexora.module.feed.infrastructure.persistence.document.FeedDocument
import com.robotutor.nexora.module.device.domain.vo.DeviceId
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.common.persistence.mapper.DocumentMapper

object FeedDocumentMapper : DocumentMapper<FeedAggregate, FeedDocument> {
    override fun toMongoDocument(domain: FeedAggregate): FeedDocument {
        return FeedDocument(
            id = domain.getObjectId(),
            feedId = domain.feedId.value,
            deviceId = domain.deviceId.value,
            premisesId = domain.premisesId.value,
            type = domain.type,
            min = domain.range.min,
            max = domain.range.max,
            mode = domain.range.mode,
            value = domain.getValue(),
            createdAt = domain.createdAt,
            updatedAt = domain.getUpdatedAt(),
            version = domain.getVersion(),
        )
    }

    override fun toDomainModel(document: FeedDocument): FeedAggregate {
        return FeedAggregate.create(
            feedId = FeedId(document.feedId),
            deviceId = DeviceId(document.deviceId),
            premisesId = PremisesId(document.premisesId),
            type = document.type,
            range = FeedValueRange(document.mode, document.min, document.max),
            createdAt = document.createdAt,
            value = document.value,
            updatedAt = document.updatedAt
        ).setObjectIdAndVersion(document.id, document.version)
    }
}

