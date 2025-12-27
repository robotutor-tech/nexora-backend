package com.robotutor.nexora.context.user.application.command

import com.robotutor.nexora.shared.application.command.Query
import com.robotutor.nexora.shared.domain.vo.AccountId

data class GetUserQuery(val accountId: AccountId) : Query
