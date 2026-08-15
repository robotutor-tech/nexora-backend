package com.robotutor.nexora.module.identity.application.command

import com.robotutor.nexora.module.identity.domain.vo.SessionId
import com.robotutor.nexora.shared.domain.vo.AccountData

data class CreateSessionCommand(
    val accountData: AccountData,
    val sessionId: SessionId,
)
