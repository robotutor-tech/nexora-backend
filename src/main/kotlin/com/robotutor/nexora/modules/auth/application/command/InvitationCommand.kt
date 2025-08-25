package com.robotutor.nexora.modules.auth.application.command

import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.ZoneId

data class InvitationCommand(
    val zoneId: ZoneId,
    val name: Name
)
