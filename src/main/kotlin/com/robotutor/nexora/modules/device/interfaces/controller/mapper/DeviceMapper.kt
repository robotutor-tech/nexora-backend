package com.robotutor.nexora.modules.device.interfaces.controller.mapper

import com.robotutor.nexora.common.security.models.InvitationData
import com.robotutor.nexora.modules.device.application.command.CreateDeviceCommand
import com.robotutor.nexora.modules.device.domain.model.Device
import com.robotutor.nexora.modules.device.interfaces.controller.dto.DeviceRequest
import com.robotutor.nexora.modules.device.interfaces.controller.dto.DeviceResponse

object DeviceMapper {
    fun toCreateDeviceCommand(deviceRequest: DeviceRequest, invitationData: InvitationData): CreateDeviceCommand {
        return CreateDeviceCommand(
            premisesId = invitationData.premisesId,
            name = invitationData.name,
            modelNo = deviceRequest.modelNo,
            serialNo = deviceRequest.serialNo,
            type = deviceRequest.deviceType,
            createdBy = invitationData.invitedBy,
        )
    }

    fun toDeviceResponse(device: Device): DeviceResponse {
        return DeviceResponse(
            deviceId = device.deviceId.value,
            premisesId = device.premisesId.value,
            name = device.name,
            modelNo = device.modelNo,
            serialNo = device.serialNo,
            type = device.type,
            state = device.state,
            health = device.health,
            feeds = device.feedIds.asList().map { it.value }
        )
    }
}