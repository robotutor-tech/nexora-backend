package com.robotutor.nexora.context.device.domain.exception

import com.robotutor.nexora.shared.domain.exception.ServiceError


enum class DeviceError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0401("NEXORA-0401", "Device registration denied"),
    NEXORA0402("NEXORA-0402", "Device metadata update denied"),
    NEXORA0403("NEXORA-0403", "Device not in COMMISSIONED state."),
}