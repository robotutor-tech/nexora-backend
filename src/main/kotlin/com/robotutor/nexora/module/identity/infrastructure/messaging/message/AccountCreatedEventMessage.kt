package com.robotutor.nexora.module.identity.infrastructure.messaging.message

import com.robotutor.nexora.shared.message.config.EventName

sealed interface AccountCreatedEventMessage : IdentityEventMessage {
    val accountId: String
}

data class UserAccountCreatedEventMessage(val userId: String, override val accountId: String) :
    AccountCreatedEventMessage {
    override val eventName: EventName = EventName.USER_ACCOUNT_CREATED
}

data class DeviceAccountCreatedEventMessage(val deviceId: String, override val accountId: String) :
    AccountCreatedEventMessage {
    override val eventName: EventName = EventName.DEVICE_ACCOUNT_CREATED
}
