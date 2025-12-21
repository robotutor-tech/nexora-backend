package com.robotutor.nexora.context.device.interfaces.controller.mapper

import com.robotutor.nexora.context.device.application.command.ActivateDeviceCommand
import com.robotutor.nexora.context.device.application.command.GetDevicesQuery
import com.robotutor.nexora.context.device.application.command.RegisterDeviceCommand
import com.robotutor.nexora.context.device.application.command.UpdateMetaDataCommand
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.aggregate.DeviceMetadata
import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.context.device.domain.vo.ModelNo
import com.robotutor.nexora.context.device.domain.vo.SerialNo
import com.robotutor.nexora.context.device.interfaces.controller.view.ActivateDeviceRequest
import com.robotutor.nexora.context.device.interfaces.controller.view.DeviceMetaDataRequest
import com.robotutor.nexora.context.device.interfaces.controller.view.DeviceMetaDataResponse
import com.robotutor.nexora.context.device.interfaces.controller.view.DeviceResponse
import com.robotutor.nexora.context.device.interfaces.controller.view.RegisterDeviceRequest
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.ZoneId
import com.robotutor.nexora.shared.interfaces.view.AuthorizedResources

object DeviceMapper {
    fun toRegisterDeviceCommand(request: RegisterDeviceRequest, actorData: ActorData): RegisterDeviceCommand {
        return RegisterDeviceCommand(
            accountId = AccountId(request.accountId),
            name = Name(request.name),
            zoneId = ZoneId(request.zoneId),
            premisesId = actorData.premisesId,
            registeredBy = actorData.actorId,
        )
    }

    fun toDeviceResponse(device: DeviceAggregate): DeviceResponse {
        return DeviceResponse(
            deviceId = device.deviceId.value,
            accountId = device.accountId.value,
            premisesId = device.premisesId.value,
            name = device.getName().value,
            state = device.getState(),
            health = device.getHealth(),
            feeds = device.getFeedIds().map { it.value },
            metaData = device.getMetadata()?.let { toDeviceMetaDataResponse(it) },
            zoneId = device.zoneId.value,
            registeredBy = device.registeredBy.value,
            createdAt = device.createdAt,
            updatedAt = device.getUpdatedAt()
        )
    }

    private fun toDeviceMetaDataResponse(deviceMetaData: DeviceMetadata): DeviceMetaDataResponse {
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
            metaData = DeviceMetadata(
                osName = Name(request.osName),
                osVersion = Name(request.osVersion),
                modelNo = ModelNo(request.modelNo),
                serialNo = SerialNo(request.serialNo)
            )
        )
    }

    fun toGetDevicesQuery(resources: AuthorizedResources, actorData: ActorData): GetDevicesQuery {
        return GetDevicesQuery(actorData.actorId, resources.toResources(DeviceId::class.java))
    }

    fun toUpdateMetaDataCommand(metadata: DeviceMetaDataRequest, accountData: AccountData): UpdateMetaDataCommand {
        return UpdateMetaDataCommand(
            accountId = accountData.accountId,
            metadata = DeviceMetadata(
                modelNo = ModelNo(metadata.modelNo),
                serialNo = SerialNo(metadata.serialNo),
                osName = Name(metadata.osName),
                osVersion = Name(metadata.osVersion),
            )
        )
    }
}