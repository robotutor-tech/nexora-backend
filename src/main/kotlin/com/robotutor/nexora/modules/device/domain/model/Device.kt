package com.robotutor.nexora.modules.device.domain.model

import com.robotutor.nexora.modules.device.domain.event.DeviceCreatedEvent
import com.robotutor.nexora.shared.domain.event.DomainAggregate
import com.robotutor.nexora.shared.domain.event.ResourceCreatedEvent
import com.robotutor.nexora.shared.domain.model.*
import java.time.Instant

class Device(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    val name: Name,
    val modelNo: ModelNo,
    val serialNo: SerialNo,
    val type: DeviceType,
    var feedIds: FeedIds = FeedIds(emptyList()),
    val state: DeviceState = DeviceState.ACTIVE,
    val health: DeviceHealth = DeviceHealth.OFFLINE,
    val os: DeviceOS? = null,
    val createdBy: ActorId,
    val createdAt: Instant = Instant.now(),
    val version: Long? = null
) : DomainAggregate() {

    fun updateFeedIds(feedIds: FeedIds): Device {
        this.feedIds = feedIds
        return this
    }

    companion object {
        fun create(
            deviceId: DeviceId,
            premisesId: PremisesId,
            name: Name,
            modelNo: ModelNo,
            serialNo: SerialNo,
            type: DeviceType,
            createdBy: ActorId,
            zoneId: ZoneId,
        ): Device {
            val device = Device(
                deviceId = deviceId,
                premisesId = premisesId,
                name = name,
                modelNo = modelNo,
                serialNo = serialNo,
                type = type,
                createdBy = createdBy
            )
            device.addDomainEvent(ResourceCreatedEvent(ResourceType.DEVICE, device.deviceId))
            device.addDomainEvent(DeviceCreatedEvent(device.deviceId, device.modelNo, zoneId))
            return device
        }
    }
}

enum class DeviceState {
    ACTIVE,
    INACTIVE,
}

enum class DeviceType {
    DEVICE,
    LOCAL_SERVER,
    SERVER
}

enum class DeviceHealth {
    ONLINE,
    OFFLINE,
}

data class DeviceOS(val name: Name, val version: String)
