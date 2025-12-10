package com.robotutor.nexora.context.iam.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.ValueObject
import java.time.Instant

data class TokenValue(val value: String) : ValueObject() {

    init {
        validate()
    }

    companion object {
        fun generate(length: Int = 120): TokenValue {
            val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9') + "_-".split("")
            val token = List(length) { chars.random() }.joinToString("")
            val fullToken = token + Instant.now().epochSecond.toString()
            return TokenValue(fullToken.substring(fullToken.length - length))
        }
    }

    override fun validate() {
        validation(value.isNotBlank()) { "Token value must not be blank" }
    }
}
