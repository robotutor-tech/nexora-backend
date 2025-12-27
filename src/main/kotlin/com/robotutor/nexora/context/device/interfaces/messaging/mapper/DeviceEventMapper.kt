package com.robotutor.nexora.context.device.interfaces.messaging.mapper

import com.robotutor.nexora.context.device.application.command.ActivateDeviceCommand
import com.robotutor.nexora.context.device.application.command.CompensateDeviceCommand
import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.context.device.interfaces.messaging.message.ActivateDeviceMessage
import com.robotutor.nexora.context.device.interfaces.messaging.message.CompensateDeviceMessage
import com.robotutor.nexora.shared.domain.vo.principal.ActorData

object DeviceEventMapper {

    fun toActivateDeviceCommand(eventMessage: ActivateDeviceMessage, actorData: ActorData): ActivateDeviceCommand {
        return ActivateDeviceCommand(DeviceId(eventMessage.deviceId), actorData.premisesId)
    }

    fun toCompensateDeviceCommand(eventMessage: CompensateDeviceMessage): CompensateDeviceCommand {
        return CompensateDeviceCommand(DeviceId(eventMessage.deviceId))
    }

}