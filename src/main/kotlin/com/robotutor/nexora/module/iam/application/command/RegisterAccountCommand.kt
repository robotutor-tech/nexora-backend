package com.robotutor.nexora.module.iam.application.command

import com.robotutor.nexora.module.iam.domain.vo.CredentialId
import com.robotutor.nexora.module.iam.domain.vo.CredentialKind
import com.robotutor.nexora.module.iam.domain.vo.CredentialSecret
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.principal.AccountType
import com.robotutor.nexora.shared.domain.vo.principal.PrincipalId

data class RegisterAccountCommand(
    val credentialId: CredentialId,
    val secret: CredentialSecret,
    val kind: CredentialKind,
    val type: AccountType,
    val principalId: PrincipalId,
    val createdBy: ActorId?
) : Command
