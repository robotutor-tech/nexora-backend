package com.robotutor.nexora.common.resolver.client.view

import com.robotutor.nexora.module.iam.domain.aggregate.AccountStatus
import com.robotutor.nexora.shared.domain.vo.principal.AccountType
import java.time.Instant

data class ResourceResponse(
    val resourceId: String,
)