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
                AccountType.USER -> Email.of(credentialId)
            }
        }
    }
}


@JvmInline
value class Email private constructor(override val value: String) : CredentialId {

    init {
        validation(value.isBlank()) { "Email is required" }
        validation(value.length > 64) { "Email must not exceed 64 characters" }
        validation(!EMAIL_REGEX.matches(value)) { "Email should be valid" }
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

        fun of(value: String): Email {
            return Email(value.trim().lowercase())
        }
    }
}

data class ApiKey(override val value: String) : CredentialId {
    init {
        validation(value.isBlank()) { "Api Key must not be blank" }
    }
}

