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
