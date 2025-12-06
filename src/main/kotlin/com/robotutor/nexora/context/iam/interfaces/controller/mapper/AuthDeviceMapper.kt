package com.robotutor.nexora.context.iam.interfaces.controller.mapper

import com.robotutor.nexora.context.iam.application.command.AuthDeviceRegisterCommand
import com.robotutor.nexora.context.iam.application.command.DeviceLoginCommand
import com.robotutor.nexora.context.iam.domain.entity.AuthDevice
import com.robotutor.nexora.context.iam.domain.entity.DeviceSecret
import com.robotutor.nexora.context.iam.interfaces.controller.view.AuthDeviceRegisterRequest
import com.robotutor.nexora.context.iam.interfaces.controller.view.AuthDeviceResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.DeviceLoginRequest
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.RoleId

object AuthDeviceMapper {

    fun toDeviceLoginCommand(deviceLoginRequest: DeviceLoginRequest): DeviceLoginCommand {
        return DeviceLoginCommand(
            deviceId = DeviceId(deviceLoginRequest.deviceId),
            deviceSecret = DeviceSecret(deviceLoginRequest.secret)
        )
    }

    fun toAuthDeviceRegisterCommand(request: AuthDeviceRegisterRequest): AuthDeviceRegisterCommand {
        return AuthDeviceRegisterCommand(
            deviceId = DeviceId(request.deviceId),
            actorId = ActorId(request.actorId),
            roleId = RoleId(request.roleId)
        )
    }

    fun toAuthDeviceResponse(authDevice: AuthDevice): AuthDeviceResponse {
        return AuthDeviceResponse(
            deviceId = authDevice.deviceId.value,
            deviceSecret = authDevice.secret.value
        )
    }
}