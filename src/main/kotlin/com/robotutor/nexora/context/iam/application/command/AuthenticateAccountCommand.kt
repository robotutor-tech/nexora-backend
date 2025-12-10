package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.common.security.domain.vo.AccountData
import com.robotutor.nexora.context.iam.domain.entity.DeviceSecret
import com.robotutor.nexora.context.iam.domain.vo.CredentialId
import com.robotutor.nexora.context.iam.domain.vo.CredentialKind
import com.robotutor.nexora.context.iam.domain.vo.CredentialSecret
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.domain.vo.PremisesId

data class AuthenticateAccountCommand(
    val credentialId: CredentialId,
    val secret: CredentialSecret,
    val kind: CredentialKind
)

data class AuthenticateActorCommand(val premisesId: PremisesId, val token: TokenValue, val accountData: AccountData)
data class DeviceLoginCommand(val deviceId: DeviceId, val deviceSecret: DeviceSecret)
data class AuthDeviceRegisterCommand(val deviceId: DeviceId, val actorId: ActorId, val roleId: RoleId)
