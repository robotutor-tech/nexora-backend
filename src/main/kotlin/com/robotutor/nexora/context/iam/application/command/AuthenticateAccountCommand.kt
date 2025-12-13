package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.context.iam.domain.vo.CredentialId
import com.robotutor.nexora.context.iam.domain.vo.CredentialKind
import com.robotutor.nexora.context.iam.domain.vo.CredentialSecret
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import com.robotutor.nexora.shared.domain.vo.PremisesId

data class AuthenticateAccountCommand(
    val credentialId: CredentialId,
    val secret: CredentialSecret,
    val kind: CredentialKind
)

data class AuthenticateActorCommand(val premisesId: PremisesId, val token: TokenValue, val accountData: AccountData)
