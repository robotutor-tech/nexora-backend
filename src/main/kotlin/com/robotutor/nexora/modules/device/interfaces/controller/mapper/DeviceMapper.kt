package com.robotutor.nexora.modules.device.interfaces.controller.mapper

import com.robotutor.nexora.modules.device.application.command.CreateDeviceCommand
import com.robotutor.nexora.modules.device.application.facade.dto.DeviceTokens
import com.robotutor.nexora.modules.device.domain.model.Device
import com.robotutor.nexora.modules.device.interfaces.controller.dto.DeviceRequest
import com.robotutor.nexora.modules.device.interfaces.controller.dto.DeviceResponse
import com.robotutor.nexora.modules.device.interfaces.controller.dto.DeviceTokensResponse
import com.robotutor.nexora.modules.seed.SeedData
import com.robotutor.nexora.shared.domain.model.ModelNo
import com.robotutor.nexora.shared.domain.model.SerialNo

object DeviceMapper {
    fun toCreateDeviceCommand(deviceRequest: DeviceRequest): CreateDeviceCommand {
        return CreateDeviceCommand(
            modelNo = ModelNo(deviceRequest.modelNo),
            serialNo = SerialNo(deviceRequest.serialNo),
            type = SeedData.getDeviceType(),
        )
    }

    fun toDeviceResponse(device: Device): DeviceResponse {
        return DeviceResponse(
            deviceId = device.deviceId.value,
            premisesId = device.premisesId.value,
            name = device.name.value,
            modelNo = device.modelNo.value,
            serialNo = device.serialNo.value,
            type = device.type,
            state = device.state,
            health = device.health,
            feeds = device.feedIds.feeds.map { it.value }
        )
    }

    fun toDeviceTokenResponse(deviceTokens: DeviceTokens): DeviceTokensResponse {
        return DeviceTokensResponse(
            token = deviceTokens.token,
            refreshToken = deviceTokens.refreshToken
        )
    }
}