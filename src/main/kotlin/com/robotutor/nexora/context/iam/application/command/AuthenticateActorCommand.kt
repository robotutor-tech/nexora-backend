package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.PremisesId

data class AuthenticateActorCommand(val premisesId: PremisesId, val token: TokenValue, val accountData: AccountData)
