package com.robotutor.nexora.module.device.application.command

import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ZoneId

data class RegisterDeviceCommand(
    val name: Name,
    val zoneId: ZoneId,
    val premisesId: PremisesId,
    val registeredBy: ActorId
) : Command
