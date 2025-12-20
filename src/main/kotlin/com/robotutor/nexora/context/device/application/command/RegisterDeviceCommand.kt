package com.robotutor.nexora.context.device.application.command

import com.robotutor.nexora.shared.domain.vo.ZoneId
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId

data class RegisterDeviceCommand(
    val accountId: AccountId,
    val name: Name,
    val zoneId: ZoneId,
    val premisesId: PremisesId,
    val registeredBy: ActorId
) : Command
