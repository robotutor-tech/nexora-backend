package com.robotutor.nexora.modules.device.adapters.persistence.model

import com.robotutor.nexora.modules.device.domain.model.Device
import com.robotutor.nexora.modules.device.domain.model.DeviceHealth
import com.robotutor.nexora.modules.device.domain.model.DeviceOS
import com.robotutor.nexora.modules.device.domain.model.DeviceState
import com.robotutor.nexora.modules.device.domain.model.DeviceType
import com.robotutor.nexora.shared.adapters.persistence.model.MongoDocument
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@TypeAlias("DeviceDocument")
@Document(collection = "devices")
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
