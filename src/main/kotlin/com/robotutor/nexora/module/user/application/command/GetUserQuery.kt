package com.robotutor.nexora.module.user.application.command

import com.robotutor.nexora.shared.application.command.Query
import com.robotutor.nexora.shared.domain.vo.principal.PrincipalId

data class GetUserQuery(val principalId: PrincipalId) : Query
