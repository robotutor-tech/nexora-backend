package com.robotutor.nexora.module.identity.application.command

import com.robotutor.nexora.module.identity.domain.vo.CredentialKind
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import com.robotutor.nexora.shared.domain.vo.principal.SubjectId

data class RotateCredentialCommand(
    val subjectId: SubjectId,
    val actorData: ActorData,
    val kind: CredentialKind
) : Command
