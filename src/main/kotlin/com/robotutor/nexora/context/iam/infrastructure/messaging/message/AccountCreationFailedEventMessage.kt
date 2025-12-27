package com.robotutor.nexora.context.iam.infrastructure.messaging.message

sealed class AccountRegistrationFailedEventMessage(type: String) :
    IAMEventMessage("account.registration.failed.$type")

class UserAccountRegistrationFailedEventMessage(val userId: String) :
    AccountRegistrationFailedEventMessage("user")

class DeviceAccountRegistrationFailedEventMessage(val deviceId: String) :
    AccountRegistrationFailedEventMessage("device")
