package com.robotutor.nexora.modules.device.adapters.persistence.model

import com.robotutor.nexora.modules.device.domain.model.Device
import com.robotutor.nexora.modules.device.domain.model.DeviceHealth
import com.robotutor.nexora.modules.device.domain.model.DeviceOS
import com.robotutor.nexora.modules.device.domain.model.DeviceState
import com.robotutor.nexora.modules.device.domain.model.DeviceType
import com.robotutor.nexora.modules.device.domain.model.FeedIds
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.model.ModelNo
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.SerialNo
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
) {
    companion object {
        fun from(device: Device): DeviceDocument {
            return DeviceDocument(
                deviceId = device.deviceId.value,
                premisesId = device.premisesId.value,
                name = device.name.value,
                modelNo = device.modelNo.value,
                serialNo = device.serialNo.value,
                type = device.type,
                feedIds = device.feedIds.asList().map { it.value },
                state = device.state,
                health = device.health,
                os = device.os,
                createdBy = device.createdBy.value,
                createdAt = device.createdAt,
                version = device.version
            )
        }
    }

    fun toDomainModel(): Device {
        return Device(
            deviceId = DeviceId(deviceId),
            premisesId = PremisesId(premisesId),
            name = Name(name),
            modelNo = ModelNo(modelNo),
            serialNo = SerialNo(serialNo),
            type = type,
            feedIds = FeedIds(feedIds.map { FeedId(it) }),
            state = state,
            health = health,
            os = os,
            createdBy = ActorId(createdBy),
            createdAt = createdAt,
            version = version
        )
    }
}