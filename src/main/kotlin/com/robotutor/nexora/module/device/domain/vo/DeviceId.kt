package com.robotutor.nexora.module.device.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.Identifier
import com.robotutor.nexora.shared.domain.vo.ValueObject
import java.util.UUID

data class DeviceId(override val value: String) : Identifier, ValueObject {
    init {
        validation(value.isBlank()) { "Device id must not be blank" }
    }

    companion object {
        fun generate(): DeviceId {
            return DeviceId(UUID.randomUUID().toString())
        }
    }

    override fun toString(): String {
        return value
    }
}