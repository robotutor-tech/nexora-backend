package com.robotutor.nexora.modules.device.domain.model

import com.robotutor.nexora.shared.domain.model.*
import java.time.Instant

class Device(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    val name: String,
    val modelNo: String,
    val serialNo: String,
    val type: DeviceType,
    var feedIds: FeedIds = FeedIds(emptyList()),
    val state: DeviceState = DeviceState.ACTIVE,
    val health: DeviceHealth = DeviceHealth.OFFLINE,
    val os: DeviceOS? = null,
    val createdBy: ActorId,
    val createdAt: Instant = Instant.now(),
    val version: Long? = null
) {
    fun updateFeedIds(feedIds: FeedIds): Device {
        this.feedIds = feedIds
        return this
    }

    companion object {
        fun from(deviceId: DeviceId, deviceDetails: DeviceDetails): Device {
            return Device(
                deviceId = deviceId,
                premisesId = deviceDetails.premisesId,
                name = deviceDetails.name,
                modelNo = deviceDetails.modelNo,
                serialNo = deviceDetails.serialNo,
                type = deviceDetails.type,
                feedIds = deviceDetails.feeds,
                state = deviceDetails.state,
                health = deviceDetails.health,
                os = deviceDetails.os,
                createdBy = deviceDetails.createdBy,
                createdAt = deviceDetails.createdAt,
                version = deviceDetails.version
            )
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

data class DeviceOS(val name: String, val version: String)
