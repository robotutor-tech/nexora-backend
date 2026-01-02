package com.robotutor.nexora.module.iam.application.command

import com.robotutor.nexora.module.iam.domain.vo.TokenValue
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.principal.AccountData

data class AuthenticateActorCommand(val premisesId: PremisesId, val token: TokenValue, val accountData: AccountData)
