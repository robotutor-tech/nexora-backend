package com.robotutor.nexora.common.security.infrastructure.facade.view

import com.robotutor.nexora.context.iam.domain.aggregate.AccountStatus
import com.robotutor.nexora.shared.domain.vo.AccountType
import java.time.Instant

data class AccountResponse(
    val accountId: String,
    val type: AccountType,
    val status: AccountStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
)