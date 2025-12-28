package com.robotutor.nexora.context.iam.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.ValueObject
import java.time.Instant

data class Credential(
    val kind: CredentialKind,
    val credentialId: CredentialId,
    val secret: HashedCredentialSecret,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val metadata: Map<String, String> = emptyMap()
) : ValueObject {
    fun rotate(newSecret: HashedCredentialSecret): Credential {
        return copy(secret = newSecret, updatedAt = Instant.now())
    }
}

enum class CredentialKind {
    API_SECRET,
    PASSWORD,
}

data class CredentialSecret(val value: String) : ValueObject {
    init {
        validation(value.isBlank()) { "Credential secret must not be blank" }
    }

    companion object {
        fun generate(): CredentialSecret {
            return CredentialSecret(TokenValue.generate(54).value)
        }
    }

    override fun equals(other: Any?): Boolean = other is CredentialSecret && other.value == value
    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = value
}

data class HashedCredentialSecret(val value: String) : ValueObject {
    init {
        validation(value.isBlank()) { "Hashed Credential secret must not be blank" }
    }
}

data class CredentialId(val value: String) : ValueObject {
    init {
        validation(value.isBlank()) { "Credential id must not be blank" }
    }
}