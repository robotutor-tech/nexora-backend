package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.context.iam.domain.vo.CredentialId
import com.robotutor.nexora.context.iam.domain.vo.CredentialSecret

data class AuthenticateAccountCommand(
    val credentialId: CredentialId,
    val secret: CredentialSecret,
)

