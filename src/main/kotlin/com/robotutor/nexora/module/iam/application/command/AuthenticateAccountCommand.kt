package com.robotutor.nexora.module.iam.application.command

import com.robotutor.nexora.module.iam.domain.vo.CredentialId
import com.robotutor.nexora.module.iam.domain.vo.CredentialSecret

data class AuthenticateAccountCommand(
    val credentialId: CredentialId,
    val secret: CredentialSecret,
)

