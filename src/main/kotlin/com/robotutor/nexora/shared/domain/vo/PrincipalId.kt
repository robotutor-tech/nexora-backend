package com.robotutor.nexora.shared.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import java.util.UUID

sealed interface PrincipalId : Identifier
sealed interface SubjectId : Identifier {
    companion object {
        fun from(accountType: AccountType, principalId: String): SubjectId {
            return when (accountType) {
                AccountType.DEVICE -> DeviceId(principalId)
                AccountType.USER -> UserId(principalId)
            }
        }
    }
}

data class ActorId(override val value: String) : PrincipalId {
    init {
        validation(value.isBlank()) { "Actor id must not be blank" }
    }

    companion object {
        fun generate() = ActorId(value = UUID.randomUUID().toString())
    }
}

data class UserId(override val value: String) : PrincipalId, SubjectId {
    init {
        validation(value.isBlank()) { "User id must not be blank" }
    }

    companion object {
        fun generate(): UserId {
            return UserId(value = "U-" + UUID.randomUUID().toString())
        }
    }
}

data class DeviceId(override val value: String) : PrincipalId, SubjectId {
    init {
        validation(value.isBlank()) { "Device id must not be blank" }
    }

    companion object {
        fun generate(): DeviceId {
            return DeviceId("D-" + UUID.randomUUID().toString())
        }
    }
}



