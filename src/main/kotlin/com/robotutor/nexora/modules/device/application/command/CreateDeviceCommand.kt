package com.robotutor.nexora.modules.device.application.command

import com.robotutor.nexora.modules.device.domain.entity.DeviceType
import com.robotutor.nexora.shared.domain.model.ModelNo
import com.robotutor.nexora.shared.domain.model.SerialNo

data class CreateDeviceCommand(
    val modelNo: ModelNo,
    val serialNo: SerialNo,
    val type: DeviceType,
)
