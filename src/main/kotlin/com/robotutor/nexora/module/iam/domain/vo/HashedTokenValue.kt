package com.robotutor.nexora.module.iam.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.ValueObject
import java.security.MessageDigest
import java.util.Base64


data class HashedTokenValue(val hashedValue: String) : ValueObject {
    init {
        validation(hashedValue.isBlank()) { "Hashed token value must not be blank" }
    }

    companion object {
        fun create(tokenValue: TokenValue): HashedTokenValue {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashedBytes = digest.digest(tokenValue.value.toByteArray(Charsets.UTF_8))
            val hashValue = Base64.getUrlEncoder().withoutPadding().encodeToString(hashedBytes)
            return HashedTokenValue(hashedValue = hashValue)
        }
    }

}