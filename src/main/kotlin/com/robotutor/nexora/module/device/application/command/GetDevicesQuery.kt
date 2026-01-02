package com.robotutor.nexora.module.device.application.command

import com.robotutor.nexora.module.device.domain.vo.DeviceId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.Resources

data class GetDevicesQuery(val actorId: ActorId, val resources: Resources<DeviceId>)
data class GetDeviceQuery(val deviceId: DeviceId)
