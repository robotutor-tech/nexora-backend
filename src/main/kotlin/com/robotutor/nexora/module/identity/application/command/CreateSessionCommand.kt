package com.robotutor.nexora.module.identity.application.command

import com.robotutor.nexora.module.identity.domain.vo.SessionId
import com.robotutor.nexora.shared.domain.vo.PrincipalData

data class CreateSessionCommand(
    val principalData: PrincipalData,
    val sessionId: SessionId,
)
