package com.robotutor.nexora.modules.device.adapters.persistence.mapper

import com.robotutor.nexora.modules.device.adapters.persistence.model.DeviceDocument
import com.robotutor.nexora.modules.device.domain.model.Device
import com.robotutor.nexora.modules.device.domain.model.FeedIds
import com.robotutor.nexora.shared.adapters.persistence.mapper.DocumentMapper
import com.robotutor.nexora.shared.domain.model.*
import org.springframework.stereotype.Component

@Component
class DeviceDocumentMapper : DocumentMapper<Device, DeviceDocument> {
    override fun toMongoDocument(domain: Device): DeviceDocument {
        return DeviceDocument(
            id = null,
            deviceId = domain.deviceId.value,
            premisesId = domain.premisesId.value,
            name = domain.name.value,
            modelNo = domain.modelNo.value,
            serialNo = domain.serialNo.value,
            type = domain.type,
            feedIds = domain.feedIds.feeds.map { it.value },
            state = domain.state,
            health = domain.health,
            os = domain.os,
            createdBy = domain.createdBy.value,
            createdAt = domain.createdAt,
            version = domain.version
        )
    }

    override fun toDomainModel(document: DeviceDocument): Device {
        return Device(
            deviceId = DeviceId(document.deviceId),
            premisesId = PremisesId(document.premisesId),
            name = Name(document.name),
            modelNo = ModelNo(document.modelNo),
            serialNo = SerialNo(document.serialNo),
            type = document.type,
            feedIds = FeedIds(document.feedIds.map { FeedId(it) }),
            state = document.state,
            health = document.health,
            os = document.os,
            createdBy = ActorId(document.createdBy),
            createdAt = document.createdAt,
            version = document.version
        )
    }
}

