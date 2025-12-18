package com.robotutor.nexora.context.device.interfaces.controller.mapper

import com.robotutor.nexora.context.device.application.command.ActivateDeviceCommand
import com.robotutor.nexora.context.device.application.command.RegisterDeviceCommand
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.aggregate.DeviceMetaData
import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.context.device.domain.vo.ModelNo
import com.robotutor.nexora.context.device.domain.vo.SerialNo
import com.robotutor.nexora.context.device.interfaces.controller.dto.ActivateDeviceRequest
import com.robotutor.nexora.context.device.interfaces.controller.dto.DeviceMetaDataResponse
import com.robotutor.nexora.context.device.interfaces.controller.dto.DeviceResponse
import com.robotutor.nexora.context.device.interfaces.controller.dto.RegisterDeviceRequest
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.ZoneId

object DeviceMapper {
    fun toRegisterDeviceCommand(request: RegisterDeviceRequest, actorData: ActorData): RegisterDeviceCommand {
        return RegisterDeviceCommand(
            name = Name(request.name),
            zoneId = ZoneId(request.zoneId),
            premisesId = actorData.premisesId,
            registeredBy = actorData.actorId,
        )
    }

    fun toDeviceResponse(device: DeviceAggregate): DeviceResponse {
        return DeviceResponse(
            deviceId = device.deviceId.value,
            premisesId = device.premisesId.value,
            name = device.getName().value,
            state = device.getState(),
            health = device.getHealth(),
            feeds = device.getFeedIds().map { it.value },
            metaData = device.getMetaData()?.let { toDeviceMetaDataResponse(it) },
            zoneId = device.zoneId.value,
            registeredBy = device.registeredBy.value,
            createdAt = device.createdAt,
            updatedAt = device.getUpdatedAt()
        )
    }

    private fun toDeviceMetaDataResponse(deviceMetaData: DeviceMetaData): DeviceMetaDataResponse {
        return DeviceMetaDataResponse(
            osName = deviceMetaData.osName.value,
            osVersion = deviceMetaData.osVersion.value,
            modelNo = deviceMetaData.modelNo.value,
            serialNo = deviceMetaData.serialNo.value,
        )
    }

    fun toActivateDeviceCommand(
        deviceId: String,
        request: ActivateDeviceRequest,
        actorData: ActorData
    ): ActivateDeviceCommand {
        return ActivateDeviceCommand(
            premisesId = actorData.premisesId,
            deviceId = DeviceId(deviceId),
            accountId = actorData.accountId,
            accountType = actorData.type,
            metaData = DeviceMetaData(
                osName = Name(request.osName),
                osVersion = Name(request.osVersion),
                modelNo = ModelNo(request.modelNo),
                serialNo = SerialNo(request.serialNo)
            )
        )
    }
}