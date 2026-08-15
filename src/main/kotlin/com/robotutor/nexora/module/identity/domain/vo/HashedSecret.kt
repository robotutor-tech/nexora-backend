package com.robotutor.nexora.module.identity.domain.vo

import com.robotutor.nexora.shared.domain.vo.SubjectType

sealed interface HashedSecret {
    val value: String

    companion object {
        fun from(subjectType: SubjectType, secret: String): HashedSecret {
            return when (subjectType) {
                SubjectType.DEVICE -> HashedApiSecret(secret)
                SubjectType.USER -> HashedPassword(secret)
            }
        }
    }
}

data class HashedPassword(override val value: String) : HashedSecret
data class HashedApiSecret(override val value: String) : HashedSecret

