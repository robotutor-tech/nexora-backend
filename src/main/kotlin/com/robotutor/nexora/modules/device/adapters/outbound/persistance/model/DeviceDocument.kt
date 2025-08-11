package com.robotutor.nexora.modules.device.adapters.outbound.persistance.model

import com.robotutor.nexora.modules.device.domain.model.Device
import com.robotutor.nexora.modules.device.domain.model.DeviceHealth
import com.robotutor.nexora.modules.device.domain.model.DeviceOS
import com.robotutor.nexora.modules.device.domain.model.DeviceState
import com.robotutor.nexora.modules.device.domain.model.DeviceType
import com.robotutor.nexora.modules.device.domain.model.FeedIds
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.model.PremisesId
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
    var feedIds: List<String> = emptyList(),
    val state: DeviceState = DeviceState.ACTIVE,
    val health: DeviceHealth = DeviceHealth.OFFLINE,
    val os: DeviceOS? = null,
    val createdBy: String,
    val createdAt: Instant = Instant.now(),
    @Version
    val version: Long? = null
) {
    companion object {
        fun from(device: Device): DeviceDocument {
            return DeviceDocument(
                deviceId = device.deviceId.value,
                premisesId = device.premisesId.value,
                name = device.name,
                modelNo = device.modelNo,
                serialNo = device.serialNo,
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
            name = name,
            modelNo = modelNo,
            serialNo = serialNo,
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