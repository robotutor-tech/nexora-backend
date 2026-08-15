package com.robotutor.nexora.shared.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import java.util.UUID

sealed interface SubjectId : Identifier {
    companion object {
        fun from(type: SubjectType, value: String): SubjectId {
            return when (type) {
                SubjectType.USER -> UserId(value)
                SubjectType.DEVICE -> DeviceId(value)
            }
        }
    }
}

data class UserId(override val value: String) : SubjectId {
    init {
        validation(value.isBlank()) { "User id must not be blank" }
    }

    companion object {
        fun generate(): UserId {
            return UserId(value = "U-" + UUID.randomUUID().toString().substring(0, 10))
        }

        fun from(subjectId: SubjectId): UserId {
            return UserId(subjectId.value)
        }
    }
}

data class DeviceId(override val value: String) : SubjectId {
    init {
        validation(value.isBlank()) { "Device id must not be blank" }
    }

    companion object {
        fun generate(): DeviceId {
            return DeviceId("D-" + UUID.randomUUID().toString().substring(0, 10))
        }

        fun from(subjectId: SubjectId): DeviceId {
            return DeviceId(subjectId.value)
        }
    }

    override fun toString(): String {
        return value
    }
}


enum class SubjectType {
    USER,
    DEVICE
}
