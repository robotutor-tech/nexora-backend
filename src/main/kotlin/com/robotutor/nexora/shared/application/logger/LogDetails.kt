package com.robotutor.nexora.shared.application.logger

data class LogDetails(
    val message: String,
    val errorCode: String? = null,
    val correlationId: String = "missing-correlation-id",
    val premisesId: String = "missing-premises-id",
    val additionalDetails: Map<String, Any?> = emptyMap()
)