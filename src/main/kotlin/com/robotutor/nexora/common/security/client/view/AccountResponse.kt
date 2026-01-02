package com.robotutor.nexora.common.security.client.view

import com.robotutor.nexora.module.iam.domain.aggregate.AccountStatus
import com.robotutor.nexora.shared.domain.vo.principal.AccountType
import java.time.Instant

data class AccountResponse(
    val accountId: String,
    val type: AccountType,
    val principalId: String,
    val status: AccountStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
)