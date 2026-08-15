package com.robotutor.nexora.shared.application.logger

import com.robotutor.nexora.shared.domain.vo.PrincipalData

data class LogDetails(
    val message: String,
    val errorCode: String? = null,
    val correlationId: String = "missing-correlation-id",
    val principalData: PrincipalData? = null,
    val additionalDetails: Map<String, Any?> = emptyMap()
)
