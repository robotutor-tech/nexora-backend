package com.robotutor.nexora.module.zone.application.command

import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId

data class CreateZoneCommand(val name: Name, val premisesId: PremisesId, val createdBy: ActorId): Command
