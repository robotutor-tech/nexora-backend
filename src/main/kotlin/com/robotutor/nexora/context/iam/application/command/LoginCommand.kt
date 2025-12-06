package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.context.iam.domain.entity.DeviceSecret
import com.robotutor.nexora.context.iam.domain.entity.Password
import com.robotutor.nexora.shared.domain.model.*

data class LoginCommand(val email: Email, val password: Password)
data class ActorLoginCommand(val actorId: ActorId, val roleId: RoleId, val userData: UserData, val token: String)
data class DeviceLoginCommand(val deviceId: DeviceId, val deviceSecret: DeviceSecret)
data class AuthDeviceRegisterCommand(val deviceId: DeviceId, val actorId: ActorId, val roleId: RoleId)
