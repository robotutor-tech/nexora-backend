package com.robotutor.nexora.module.device.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.principal.SubjectId
import java.util.*

data class DeviceId(override val value: String) : SubjectId(value) {
    init {
        validation(value.isBlank()) { "Device id must not be blank" }
    }

    companion object {
        fun generate(): DeviceId {
            return DeviceId(UUID.randomUUID().toString())
        }

        fun from(subjectId: SubjectId): DeviceId {
            return DeviceId(subjectId.value)
        }
    }

    override fun toString(): String {
        return value
    }
}
