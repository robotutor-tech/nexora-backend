package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.context.iam.domain.vo.CredentialKind
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import com.robotutor.nexora.shared.domain.vo.principal.PrincipalId

data class RotateCredentialCommand(
    val principalId: PrincipalId,
    val actorData: ActorData,
    val kind: CredentialKind
) : Command
