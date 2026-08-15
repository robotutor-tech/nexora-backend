package com.robotutor.nexora.module.identity.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.ValueObject

sealed interface RawSecret : ValueObject {
    val value: String
}


data class RawPassword(override val value: String) : RawSecret {
    init {
        validation(value.isBlank()) { "Password must not be blank" }
    }
}

data class RawApiSecret(override val value: String) : RawSecret {
    init {
        validation(value.isBlank()) { "Api secret must not be blank" }
    }
}
