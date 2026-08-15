package com.robotutor.nexora.module.identity.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.AccountType
import com.robotutor.nexora.shared.domain.vo.ValueObject


sealed interface CredentialId : ValueObject {
    val value: String

    companion object {
        fun from(accountType: AccountType, credentialId: String): CredentialId {
            return when (accountType) {
                AccountType.DEVICE -> ApiKey(credentialId)
                AccountType.USER -> Email(credentialId)
            }
        }
    }
}


data class Email(override val value: String) : CredentialId {
    init {
        validation(value.isBlank()) { "Email must not be blank" }
    }
}

data class ApiKey(override val value: String) : CredentialId {
    init {
        validation(value.isBlank()) { "Api Key must not be blank" }
    }
}

