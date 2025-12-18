package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.ZoneId

data class InvitationCommand(
    val zoneId: ZoneId,
    val name: Name
)
