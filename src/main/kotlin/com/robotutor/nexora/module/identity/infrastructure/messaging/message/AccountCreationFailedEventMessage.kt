package com.robotutor.nexora.module.identity.infrastructure.messaging.message

import com.robotutor.nexora.shared.message.config.EventName

sealed interface AccountRegistrationFailedEventMessage : IdentityEventMessage

class UserAccountRegistrationFailedEventMessage(val userId: String) :
    AccountRegistrationFailedEventMessage {
    override val eventName: EventName = EventName.ACCOUNT_REGISTRATION_FAILED
}

class DeviceAccountRegistrationFailedEventMessage(val deviceId: String) :
    AccountRegistrationFailedEventMessage {
    override val eventName: EventName = EventName.ACCOUNT_REGISTRATION_FAILED
}
