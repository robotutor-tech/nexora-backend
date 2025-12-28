package com.robotutor.nexora.context.device.infrastructure.persistence.mapper

import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.aggregate.DeviceMetadata
import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.context.device.domain.vo.ModelNo
import com.robotutor.nexora.context.device.domain.vo.SerialNo
import com.robotutor.nexora.context.device.infrastructure.persistence.document.DeviceDocument
import com.robotutor.nexora.context.device.infrastructure.persistence.document.DeviceMetaDataDocument
import com.robotutor.nexora.shared.domain.vo.*
import com.robotutor.nexora.common.persistence.mapper.DocumentMapper

object DeviceDocumentMapper : DocumentMapper<DeviceAggregate, DeviceDocument> {
    override fun toMongoDocument(domain: DeviceAggregate): DeviceDocument {
        return DeviceDocument(
            id = domain.getObjectId(),
            deviceId = domain.deviceId.value,
            premisesId = domain.premisesId.value,
            name = domain.getName().value,
            feedIds = domain.getFeedIds().map { it.value }.toSet(),
            state = domain.getState(),
            health = domain.getHealth(),
            metaData = domain.getMetadata()?.let { toMetadataDocument(it) },
            zoneId = domain.zoneId.value,
            registeredBy = domain.registeredBy.value,
            createdAt = domain.createdAt,
            updatedAt = domain.getUpdatedAt(),
            version = domain.getVersion(),
        )
    }

    override fun toDomainModel(document: DeviceDocument): DeviceAggregate {
        return DeviceAggregate.create(
            deviceId = DeviceId(document.deviceId),
            premisesId = PremisesId(document.premisesId),
            name = Name(document.name),
            feedIds = document.feedIds.map { FeedId(it) }.toSet(),
            state = document.state,
            health = document.health,
            metaData = document.metaData?.let { toDeviceMetaData(it) },
            zoneId = ZoneId(document.zoneId),
            registeredBy = ActorId(document.registeredBy),
            createdAt = document.createdAt,
            updatedAt = document.updatedAt,
        ).setObjectIdAndVersion(document.id, document.version)
    }

    private fun toDeviceMetaData(metaDataDocument: DeviceMetaDataDocument): DeviceMetadata = DeviceMetadata(
        osName = Name(metaDataDocument.osName),
        osVersion = Name(metaDataDocument.osVersion),
        modelNo = ModelNo(metaDataDocument.modelNo),
        serialNo = SerialNo(metaDataDocument.serialNo)
    )

    private fun toMetadataDocument(metaData: DeviceMetadata): DeviceMetaDataDocument {
        return DeviceMetaDataDocument(
            osName = metaData.osName.value,
            osVersion = metaData.osVersion.value,
            modelNo = metaData.modelNo.value,
            serialNo = metaData.serialNo.value
        )
    }
}
