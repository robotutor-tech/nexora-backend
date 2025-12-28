package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.context.iam.domain.vo.CredentialKind
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.principal.ActorData

data class RotateCredentialCommand(
    val accountId: AccountId,
    val actorData: ActorData,
    val kind: CredentialKind
) : Command
