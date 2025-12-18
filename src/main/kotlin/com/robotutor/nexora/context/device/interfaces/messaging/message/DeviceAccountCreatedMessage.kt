package com.robotutor.nexora.context.device.interfaces.messaging.message

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

data class DeviceAccountCreatedMessage(val accountId: String, val credentialId: String) : EventMessage()