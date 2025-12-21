package com.robotutor.nexora.context.device.application.command

import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.Resources

data class GetDevicesQuery(val actorId: ActorId, val resources: Resources<DeviceId>)
