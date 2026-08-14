package com.robotutor.nexora.module.identity.application.command

import com.robotutor.nexora.module.identity.domain.vo.CredentialId
import com.robotutor.nexora.module.identity.domain.vo.CredentialSecret

data class AuthenticateAccountCommand(
    val credentialId: CredentialId,
    val secret: CredentialSecret,
)

