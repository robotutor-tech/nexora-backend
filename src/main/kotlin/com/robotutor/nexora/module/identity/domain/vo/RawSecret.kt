package com.robotutor.nexora.module.identity.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.ValueObject

sealed interface RawSecret : ValueObject {
    val value: String
}


class RawPassword(override val value: String) : RawSecret {
    init {
        validation(value.length !in MIN_LENGTH..MAX_LENGTH) { "Password must be between $MIN_LENGTH and $MAX_LENGTH characters" }
        validation(!value.any { it.isUpperCase() }) { "Password must contain at least 1 uppercase letter" }
        validation(!value.any { it.isLowerCase() }) { "Password must contain at least 1 lowercase letter" }
        validation(!value.any { it.isDigit() }) { "Password must contain at least 1 number" }
        validation(!value.any { !it.isLetterOrDigit() }) { "Password must contain at least 1 special character" }
    }

    companion object {
        const val MIN_LENGTH = 8
        const val MAX_LENGTH = 20
    }
}

data class RawApiSecret(override val value: String) : RawSecret {
    init {
        validation(value.isBlank()) { "Api secret must not be blank" }
    }
}
