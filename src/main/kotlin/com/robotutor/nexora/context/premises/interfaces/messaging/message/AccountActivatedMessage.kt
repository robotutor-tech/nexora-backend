package com.robotutor.nexora.context.premises.interfaces.messaging.message

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

data class AccountActivatedMessage(val accountId: String) : EventMessage()
