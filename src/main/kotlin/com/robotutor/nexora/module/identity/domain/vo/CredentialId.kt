package com.robotutor.nexora.module.identity.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.SubjectType
import com.robotutor.nexora.shared.domain.vo.ValueObject


sealed interface CredentialId : ValueObject {
    val value: String

    companion object {
        fun from(subjectType: SubjectType, credentialId: String): CredentialId {
            return when (subjectType) {
                SubjectType.DEVICE -> ApiKey(credentialId)
                SubjectType.USER -> Email(credentialId)
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

