package com.robotutor.nexora.device.models

import com.robotutor.nexora.device.controllers.view.DeviceRequest
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.UserId
import com.robotutor.nexora.security.models.PremisesActorData
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

const val DEVICE_COLLECTION = "device"

@TypeAlias("Device")
@Document(DEVICE_COLLECTION)
class Device(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val deviceId: DeviceId,
    @Indexed()
    val premisesId: PremisesId,
    val name: String,
    val modelNo: String,
    val serialNo: String,
    val state: DeviceState = DeviceState.ACTIVE,
    val health: DeviceHealth = DeviceHealth.OFFLINE,
    val os: DeviceOS? = null,
    val createdBy: UserId,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun from(deviceId: DeviceId, deviceRequest: DeviceRequest, premisesActorData: PremisesActorData): Device {
            return Device(
                deviceId = deviceId,
                premisesId = premisesActorData.premisesId,
                name = deviceRequest.name,
                modelNo = deviceRequest.modelNo,
                serialNo = deviceRequest.serialNo,
                createdBy = premisesActorData.actorId,
            )
        }
    }
}

enum class DeviceState {
    ACTIVE,
    INACTIVE,
}

enum class DeviceHealth {
    ONLINE,
    OFFLINE,
}

data class DeviceOS(val name: String, val version: String)

typealias DeviceId = String