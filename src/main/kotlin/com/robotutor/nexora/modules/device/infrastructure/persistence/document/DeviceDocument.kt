package com.robotutor.nexora.modules.device.infrastructure.persistence.document

import com.robotutor.nexora.modules.device.domain.entity.Device
import com.robotutor.nexora.modules.device.domain.entity.DeviceHealth
import com.robotutor.nexora.modules.device.domain.entity.DeviceOS
import com.robotutor.nexora.modules.device.domain.entity.DeviceState
import com.robotutor.nexora.modules.device.domain.entity.DeviceType
import com.robotutor.nexora.shared.infrastructure.persistence.model.MongoDocument
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "devices")
@TypeAlias("Device")
data class DeviceDocument(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val deviceId: String,
    @Indexed
    val premisesId: String,
    val name: String,
    val modelNo: String,
    val serialNo: String,
    val type: DeviceType,
    val feedIds: List<String>,
    val state: DeviceState,
    val health: DeviceHealth,
    val os: DeviceOS?,
    val createdBy: String,
    val createdAt: Instant,
    @Version
    val version: Long?
) : MongoDocument<Device>