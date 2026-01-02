package com.robotutor.nexora.module.device.interfaces.messaging.mapper

import com.robotutor.nexora.module.device.application.command.ActorRegisteredDeviceCommand
import com.robotutor.nexora.module.device.application.command.CompensateDeviceCommand
import com.robotutor.nexora.module.device.domain.vo.DeviceId
import com.robotutor.nexora.module.device.interfaces.messaging.message.ActorRegisteredDeviceMessage
import com.robotutor.nexora.module.device.interfaces.messaging.message.CompensateDeviceMessage
import com.robotutor.nexora.shared.domain.vo.principal.ActorData

object DeviceEventMapper {

    fun toActorRegisteredDeviceCommand(eventMessage: ActorRegisteredDeviceMessage, actorData: ActorData): ActorRegisteredDeviceCommand {
        return ActorRegisteredDeviceCommand(DeviceId(eventMessage.deviceId), actorData.premisesId)
    }

    fun toCompensateDeviceCommand(eventMessage: CompensateDeviceMessage): CompensateDeviceCommand {
        return CompensateDeviceCommand(DeviceId(eventMessage.deviceId))
    }

}