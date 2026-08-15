package com.robotutor.nexora.module.identity.interfaces.controller.view

import com.robotutor.nexora.module.identity.domain.aggregate.AccountStatus
import com.robotutor.nexora.shared.domain.vo.SubjectType
import java.time.Instant

data class AccountResponse(
    val accountId: String,
    val type: SubjectType,
    val principalId: String,
    val status: AccountStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
)
