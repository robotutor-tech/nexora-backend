package com.robotutor.nexora.device.controllers.view

import com.robotutor.nexora.device.models.Device
import com.robotutor.nexora.device.models.DeviceHealth
import com.robotutor.nexora.device.models.DeviceId
import com.robotutor.nexora.device.models.DeviceState
import com.robotutor.nexora.premises.models.PremisesId
import jakarta.validation.constraints.NotBlank

data class DeviceRequest(
    @field:NotBlank(message = "Model No is required")
    val modelNo: String,
    @field:NotBlank(message = "Name is required")
    val name: String,
    @field:NotBlank(message = "Serial no is required")
    val serialNo: String,
)

data class DeviceView(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    val name: String,
    val modelNo: String,
    val serialNo: String,
    val state: DeviceState,
    val health: DeviceHealth,
) {
    companion object {
        fun from(device: Device): DeviceView {
            return DeviceView(
                deviceId = device.deviceId,
                premisesId = device.premisesId,
                name = device.name,
                modelNo = device.modelNo,
                serialNo = device.serialNo,
                state = device.state,
                health = device.health,
            )
        }
    }
}