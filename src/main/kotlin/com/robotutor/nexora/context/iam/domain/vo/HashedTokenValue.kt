package com.robotutor.nexora.context.iam.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.ValueObject
import java.security.MessageDigest
import java.util.Base64


data class HashedTokenValue(val hashedValue: String) : ValueObject() {
    init {
        validate()
    }

    companion object {
        fun create(tokenValue: TokenValue): HashedTokenValue {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashedBytes = digest.digest(tokenValue.value.toByteArray(Charsets.UTF_8))
            val hashValue = Base64.getUrlEncoder().withoutPadding().encodeToString(hashedBytes)
            return HashedTokenValue(hashedValue = hashValue)
        }
    }

    override fun validate() {
        validation(hashedValue.isNotBlank()) { "Hashed token value must not be blank" }
    }
}