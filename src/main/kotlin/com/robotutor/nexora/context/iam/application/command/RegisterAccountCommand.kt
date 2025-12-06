package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.context.iam.domain.aggregate.AccountType
import com.robotutor.nexora.context.iam.domain.vo.CredentialId
import com.robotutor.nexora.context.iam.domain.vo.CredentialKind
import com.robotutor.nexora.context.iam.domain.vo.CredentialSecret

data class RegisterAccountCommand(
    val credentialId: CredentialId,
    val secret: CredentialSecret,
    val kind: CredentialKind,
    val type: AccountType
)
