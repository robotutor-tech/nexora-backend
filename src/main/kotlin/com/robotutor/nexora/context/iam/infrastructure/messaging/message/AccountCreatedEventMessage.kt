package com.robotutor.nexora.context.iam.infrastructure.messaging.message

import com.robotutor.nexora.shared.domain.vo.AccountId

sealed class AccountCreatedEventMessage(val accountId: String, type: String) :
    IAMEventMessage("account.registered.$type")

class UserAccountCreatedEventMessage(val userId: String, accountId: AccountId) :
    AccountCreatedEventMessage(accountId = accountId.value, "user")

class DeviceAccountCreatedEventMessage(val deviceId: String, accountId: AccountId) :
    AccountCreatedEventMessage(accountId = accountId.value, "device")
