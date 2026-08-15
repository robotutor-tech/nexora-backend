package com.robotutor.nexora.module.identity.domain.vo

import com.robotutor.nexora.shared.domain.vo.AccountType

sealed interface HashedSecret {
    val value: String

    companion object {
        fun from(accountType: AccountType, secret: String): HashedSecret {
            return when (accountType) {
                AccountType.DEVICE -> HashedApiSecret(secret)
                AccountType.USER -> HashedPassword(secret)
            }
        }
    }
}

data class HashedPassword(override val value: String) : HashedSecret
data class HashedApiSecret(override val value: String) : HashedSecret

