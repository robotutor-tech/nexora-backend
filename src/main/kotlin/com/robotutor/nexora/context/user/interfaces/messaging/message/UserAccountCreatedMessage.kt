package com.robotutor.nexora.context.user.interfaces.messaging.message

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

data class UserAccountCreatedMessage(val accountId: String, val credentialId: String) : EventMessage()

