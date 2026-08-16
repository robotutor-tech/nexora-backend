package com.robotutor.nexora.shared.domain.vo

sealed interface PrincipalData {
    val principalId: PrincipalId
    val principalType: PrincipalType
}

sealed class AccountData : PrincipalData {
    abstract val accountId: AccountId
    abstract val accountType: AccountType
    override val principalType: PrincipalType = PrincipalType.ACCOUNT
}

data class ActorData(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val accountData: AccountData,
) : PrincipalData {
    override val principalId: PrincipalId = actorId
    override val principalType: PrincipalType = PrincipalType.ACTOR
}


data class UserData(val userId: UserId, override val accountId: AccountId) : AccountData() {
    override val principalId: PrincipalId = userId
    override val accountType: AccountType = AccountType.USER

    companion object {
        fun from(accountId: AccountId, subjectId: SubjectId): UserData {
            return UserData(UserId(subjectId.value), accountId)
        }
    }
}

data class DeviceData(val deviceId: DeviceId, override val accountId: AccountId) : AccountData() {
    override val principalId: PrincipalId = deviceId
    override val accountType: AccountType = AccountType.DEVICE

    companion object {
        fun from(accountId: AccountId, subjectId: SubjectId): DeviceData {
            return DeviceData(DeviceId(subjectId.value), accountId)
        }
    }
}



