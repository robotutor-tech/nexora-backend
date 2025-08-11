package com.robotutor.nexora.modules.device.adapters.inbound.controller.mapper

import com.robotutor.nexora.modules.device.adapters.inbound.controller.dto.DeviceRequest
import com.robotutor.nexora.modules.device.adapters.inbound.controller.dto.DeviceResponse
import com.robotutor.nexora.modules.device.domain.model.Device
import com.robotutor.nexora.modules.device.domain.model.DeviceDetails
import com.robotutor.nexora.common.security.models.InvitationData
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.PremisesId

object DeviceMapper {
    fun toDeviceDetails(deviceRequest: DeviceRequest, invitationData: InvitationData): DeviceDetails {
        return DeviceDetails(
            premisesId = PremisesId(invitationData.premisesId),
            name = invitationData.name,
            modelNo = deviceRequest.modelNo,
            serialNo = deviceRequest.serialNo,
            type = deviceRequest.deviceType,
            createdBy = ActorId(invitationData.invitedBy),
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