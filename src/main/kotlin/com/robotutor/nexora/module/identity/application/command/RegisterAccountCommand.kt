package com.robotutor.nexora.module.identity.application.command

import com.robotutor.nexora.module.identity.domain.vo.CredentialId
import com.robotutor.nexora.module.identity.domain.vo.CredentialKind
import com.robotutor.nexora.module.identity.domain.vo.CredentialSecret
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.principal.SubjectType
import com.robotutor.nexora.shared.domain.vo.principal.SubjectId

data class RegisterAccountCommand(
    val credentialId: CredentialId,
    val secret: CredentialSecret,
    val kind: CredentialKind,
    val type: SubjectType,
    val subjectId: SubjectId,
    val createdBy: ActorId?
) : Command
