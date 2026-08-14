package com.robotutor.nexora.module.user.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.principal.SubjectId
import java.util.*

data class UserId(override val value: String) : SubjectId(value) {
    init {
        validation(value.isBlank()) { "User id must not be blank" }
    }

    companion object {
        fun generate() = UserId(value = UUID.randomUUID().toString())
        fun from(subjectId: SubjectId): UserId {
            return UserId(subjectId.value)
        }
    }
}
