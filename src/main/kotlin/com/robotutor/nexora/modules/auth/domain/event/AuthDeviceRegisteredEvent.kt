package com.robotutor.nexora.modules.auth.domain.event

import com.robotutor.nexora.shared.domain.model.DeviceId

data class AuthDeviceRegisteredEvent(val deviceId: DeviceId) : AuthEvent("auth-device.registered")
