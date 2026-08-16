package com.robotutor.nexora.module.identity.infrastructure.messaging.message

import com.robotutor.nexora.shared.message.config.EventName

sealed interface AccountCreationFailedEventMessage : IdentityEventMessage

class UserAccountCreationFailedEventMessage(val userId: String) :
    AccountCreationFailedEventMessage {
    override val eventName: EventName = EventName.USER_ACCOUNT_CREATION_FAILED
}

class DeviceAccountCreationFailedEventMessage(val deviceId: String) :
    AccountCreationFailedEventMessage {
    override val eventName: EventName = EventName.DEVICE_ACCOUNT_CREATION_FAILED
}
