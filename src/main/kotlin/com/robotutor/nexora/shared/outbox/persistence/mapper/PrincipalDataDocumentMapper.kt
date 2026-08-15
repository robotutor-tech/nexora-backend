package com.robotutor.nexora.shared.outbox.persistence.mapper

import com.robotutor.nexora.shared.domain.vo.*
import com.robotutor.nexora.shared.outbox.persistence.document.*

object PrincipalDataDocumentMapper {
    fun toMongoDocument(principalData: PrincipalData): PrincipalDataDocument {
        return when (principalData) {
            is ActorData -> ActorDataDocument(
                actorId = principalData.actorId.value,
                premisesId = principalData.premisesId.value,
                accountData = toAccountDataDocument(principalData.accountData),
            )

            is AccountData -> toAccountDataDocument(principalData)
        }
    }


    fun toDomainModel(principalDataDocument: PrincipalDataDocument): PrincipalData {
        return when (principalDataDocument) {
            is ActorDataDocument -> ActorData(
                actorId = ActorId(principalDataDocument.actorId),
                premisesId = PremisesId(principalDataDocument.premisesId),
                accountData = toAccountData(principalDataDocument.accountData),
            )

            is AccountDataDocument -> toAccountData(principalDataDocument)
        }
    }

    private fun toAccountData(accountData: AccountDataDocument): AccountData {
        val accountId = AccountId(accountData.accountId)
        return when (accountData) {
            is DeviceDataDocument -> DeviceData(DeviceId(accountData.principalId), accountId)
            is UserDataDocument -> UserData(UserId(accountData.principalId), accountId)
        }
    }

    private fun toAccountDataDocument(accountData: AccountData): AccountDataDocument {
        val accountId = accountData.accountId.value
        return when (accountData) {
            is DeviceData -> DeviceDataDocument(accountData.deviceId.value, accountId)
            is UserData -> UserDataDocument(accountData.userId.value, accountId)
        }
    }
}
