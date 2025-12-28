package com.robotutor.nexora.context.device.infrastructure.persistence.document

import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.aggregate.DeviceHealth
import com.robotutor.nexora.context.device.domain.aggregate.DeviceState
import com.robotutor.nexora.common.persistence.document.MongoDocument
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val DEVICE_COLLECTION = "devices"

@Document(DEVICE_COLLECTION)
@TypeAlias("Device")
data class DeviceDocument(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val deviceId: String,
    val premisesId: String,
    val name: String,
    val metaData: DeviceMetaDataDocument?,
    val state: DeviceState,
    val feedIds: Set<String>,
    val health: DeviceHealth,
    val zoneId: String,
    val registeredBy: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    @Version
    val version: Long? = null
) : MongoDocument<DeviceAggregate>

data class DeviceMetaDataDocument(
    val osName: String,
    val osVersion: String,
    val modelNo: String,
    val serialNo: String
)