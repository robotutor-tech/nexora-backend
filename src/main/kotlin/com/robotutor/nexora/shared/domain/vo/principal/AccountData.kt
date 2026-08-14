package com.robotutor.nexora.shared.domain.vo.principal

import com.robotutor.nexora.module.device.domain.vo.DeviceId
import com.robotutor.nexora.module.user.domain.vo.UserId
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId

sealed interface AccountData {
    val accountId: AccountId
    val subjectType: SubjectType
    val subjectId: SubjectId
    val accountType: AccountType
}

data class UserData(val userId: UserId, override val accountId: AccountId) : AccountData {
    override val subjectId: SubjectId = userId
    override val subjectType: SubjectType = SubjectType.USER
    override val accountType: AccountType = AccountType.USER
}

data class DeviceData(val deviceId: DeviceId, override val accountId: AccountId) : AccountData {
    override val subjectType: SubjectType = SubjectType.DEVICE
    override val subjectId: SubjectId = deviceId
    override val accountType: AccountType = AccountType.DEVICE
}

data class ActorData(
    val actorId: ActorId,
    val premisesId: PremisesId,
    override val accountId: AccountId,
    override val subjectId: SubjectId,
    override val subjectType: SubjectType
) : AccountData {
    override val accountType: AccountType = AccountType.ACTOR
}

enum class AccountType {
    USER,
    DEVICE,
    ACTOR
}

