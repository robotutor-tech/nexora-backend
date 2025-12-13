package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.context.iam.domain.vo.CredentialId
import com.robotutor.nexora.context.iam.domain.vo.CredentialKind
import com.robotutor.nexora.context.iam.domain.vo.CredentialSecret
import com.robotutor.nexora.shared.domain.vo.AccountType

data class RegisterAccountCommand(
    val credentialId: CredentialId,
    val secret: CredentialSecret,
    val kind: CredentialKind,
    val type: AccountType
)
