package com.robotutor.nexora.modules.device.domain.model

import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.PremisesId
import java.time.Instant

class DeviceDetails(
    val premisesId: PremisesId,
    val name: String,
    val modelNo: String,
    val serialNo: String,
    val type: DeviceType,
    val feeds: FeedIds = FeedIds(emptyList()),
    val state: DeviceState = DeviceState.ACTIVE,
    val health: DeviceHealth = DeviceHealth.OFFLINE,
    val os: DeviceOS? = null,
    val createdBy: ActorId,
    val createdAt: Instant = Instant.now(),
    val version: Long? = null
)