package com.robotutor.nexora.shared.application.logger

import com.robotutor.nexora.shared.domain.vo.AccountData

data class LogDetails(
    val message: String,
    val errorCode: String? = null,
    val correlationId: String = "missing-correlation-id",
    val accountData: AccountData? = null,
    val additionalDetails: Map<String, Any?> = emptyMap()
)
