package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.context.iam.domain.vo.SessionPrincipal

data class CreateSessionCommand(
    val sessionPrincipal: SessionPrincipal
)