package com.robotutor.nexora.modules.auth.application.command

import com.robotutor.nexora.modules.auth.domain.entity.Password
import com.robotutor.nexora.shared.domain.model.*

data class LoginCommand(val email: Email, val password: Password)
data class ActorLoginCommand(val actorId: ActorId, val roleId: RoleId, val userData: UserData, val token: String)
data class CreateDeviceTokenCommand(val deviceId: DeviceId)
