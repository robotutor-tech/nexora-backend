package com.robotutor.nexora.device.models

import com.robotutor.nexora.device.controllers.view.DeviceRequest
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.InvitationData
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
    val type: DeviceType,
    val state: DeviceState = DeviceState.ACTIVE,
    val health: DeviceHealth = DeviceHealth.OFFLINE,
    val os: DeviceOS? = null,
    val createdBy: ActorId,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun from(deviceId: DeviceId, deviceRequest: DeviceRequest, invitationData: InvitationData): Device {
            return Device(
                deviceId = deviceId,
                premisesId = invitationData.premisesId,
                name = invitationData.name,
                modelNo = deviceRequest.modelNo,
                serialNo = deviceRequest.serialNo,
                createdBy = invitationData.invitedBy,
                type = deviceRequest.deviceType,
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

typealias DeviceId = String