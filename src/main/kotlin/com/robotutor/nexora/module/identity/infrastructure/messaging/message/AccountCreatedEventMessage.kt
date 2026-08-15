package com.robotutor.nexora.module.identity.infrastructure.messaging.message

import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.message.config.EventName

sealed class AccountCreatedEventMessage(val accountId: String) :
    IdentityEventMessage {

}

class UserAccountCreatedEventMessage(val userId: String, accountId: AccountId) :
    AccountCreatedEventMessage(accountId.value) {
    override val eventName: EventName = EventName.ACCOUNT_CREATED
}

class DeviceAccountCreatedEventMessage(val deviceId: String, accountId: AccountId) :
    AccountCreatedEventMessage(accountId = accountId.value) {
    override val eventName: EventName = EventName.ACCOUNT_CREATED
}
