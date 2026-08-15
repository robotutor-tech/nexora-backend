package com.robotutor.nexora.module.identity.application.command

import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.SubjectId

data class RotateCredentialCommand(
    val subjectId: SubjectId,
    val actorData: ActorData,
) : Command
